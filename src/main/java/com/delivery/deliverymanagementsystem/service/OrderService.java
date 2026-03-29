package com.delivery.deliverymanagementsystem.service;

import com.delivery.deliverymanagementsystem.dto.OrderCreateDto;
import com.delivery.deliverymanagementsystem.entity.Customer;
import com.delivery.deliverymanagementsystem.entity.Order;
import com.delivery.deliverymanagementsystem.entity.OrderStatus;
import com.delivery.deliverymanagementsystem.entity.Product;
import com.delivery.deliverymanagementsystem.repository.CustomerRepository;
import com.delivery.deliverymanagementsystem.repository.OrderRepository;
import com.delivery.deliverymanagementsystem.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

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
        return orderRepository.findById(id).orElseThrow();
    }

    @Transactional
    public Order createOrder(OrderCreateDto dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId()).orElseThrow();

        List<Product> products = dto.getProductIds().stream()
                .map(id -> productRepository.findById(id).orElseThrow())
                .toList();

        Order order = new Order();
        order.setCustomer(customer);
        order.setProducts(products);
        order.setStatus(dto.getStatus());
        order.setTotalAmount(0);

        return orderRepository.save(order);
    }

    @Transactional
    public Order createOrderWithError(OrderCreateDto dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId()).orElseThrow();

        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(dto.getStatus());

        orderRepository.save(order);

        if (true) {
            throw new RuntimeException();
        }

        return order;
    }

    @Transactional
    public Order updateStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id).orElseThrow();
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public void delete(Long id) {
        orderRepository.deleteById(id);
    }
}