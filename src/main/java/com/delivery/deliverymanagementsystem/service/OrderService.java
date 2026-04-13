package com.delivery.deliverymanagementsystem.service;

import com.delivery.deliverymanagementsystem.dto.OrderCreateDto;
import com.delivery.deliverymanagementsystem.dto.OrderFilter;
import com.delivery.deliverymanagementsystem.entity.Customer;
import com.delivery.deliverymanagementsystem.entity.Order;
import com.delivery.deliverymanagementsystem.entity.OrderStatus;
import com.delivery.deliverymanagementsystem.entity.Product;
import com.delivery.deliverymanagementsystem.repository.CustomerRepository;
import com.delivery.deliverymanagementsystem.repository.OrderRepository;
import com.delivery.deliverymanagementsystem.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    private final Map<OrderFilter, List<Order>> cache = new HashMap<>();

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    public Order getById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    public List<Order> getFiltered(OrderStatus status, double amount) {
        OrderFilter key = new OrderFilter(status, amount);

        if (cache.containsKey(key)) {
            log.info("FROM CACHE");
            return cache.get(key);
        }

        List<Order> result = orderRepository.findByStatusAndAmount(status, amount);
        cache.put(key, result);

        return result;
    }

    public List<Order> getFilteredNative(OrderStatus status, double amount) {
        OrderFilter key = new OrderFilter(status, amount);

        if (cache.containsKey(key)) {
            log.info("FROM CACHE");
            return cache.get(key);
        }

        List<Order> result = orderRepository.findByStatusAndAmountNative(status.name(), amount);
        cache.put(key, result);

        return result;
    }

    public List<Order> getByCustomerName(String name, double amount) {
        OrderFilter key = new OrderFilter(null, amount);

        if (cache.containsKey(key)) {
            log.info("FROM CACHE");
            return cache.get(key);
        }

        List<Order> result = orderRepository.findByCustomerNameAndAmount(name, amount);
        cache.put(key, result);

        return result;
    }

    public List<Order> getByCustomerNameNative(String name, double amount) {
        OrderFilter key = new OrderFilter(null, amount);

        if (cache.containsKey(key)) {
            log.info("FROM CACHE");
            return cache.get(key);
        }

        List<Order> result = orderRepository.findByCustomerNameAndAmountNative(name, amount);
        cache.put(key, result);

        return result;
    }

    public Page<Order> getPaged(int page, int size) {
        return orderRepository.findAll(PageRequest.of(page, size));
    }

    @Transactional
    public Order createOrder(OrderCreateDto dto) {

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        if (dto.getProductIds() == null || dto.getProductIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "products required");
        }

        List<Product> products = new ArrayList<>();

        for (Long productId : dto.getProductIds()) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + productId));
            products.add(product);
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setProducts(products);
        order.setStatus(dto.getStatus() != null ? dto.getStatus() : OrderStatus.NEW);

        double total = 0;
        for (Product p : products) {
            total += p.getPrice();
        }

        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);

        cache.clear();

        return saved;
    }

    @Transactional
    public Order updateStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        order.setStatus(status);

        Order updated = orderRepository.save(order);

        cache.clear();

        return updated;
    }

    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }

        orderRepository.deleteById(id);

        cache.clear();
    }
}