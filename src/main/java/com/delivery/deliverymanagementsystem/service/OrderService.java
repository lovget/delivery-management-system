package com.delivery.deliverymanagementsystem.service;

import com.delivery.deliverymanagementsystem.dto.OrderCreateDto;
import com.delivery.deliverymanagementsystem.dto.OrderFilter;
import com.delivery.deliverymanagementsystem.entity.Category;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private static final String FROM_CACHE_LOG = "FROM CACHE";

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
        OrderFilter key = new OrderFilter(status, null, amount, "status-jpql");

        if (cache.containsKey(key)) {
            log.info(FROM_CACHE_LOG);
            return cache.get(key);
        }

        List<Order> result = orderRepository.findByStatusAndAmount(status, amount);
        cache.put(key, result);

        return result;
    }

    public List<Order> getFilteredNative(OrderStatus status, double amount) {
        OrderFilter key = new OrderFilter(status, null, amount, "status-native");

        if (cache.containsKey(key)) {
            log.info(FROM_CACHE_LOG);
            return cache.get(key);
        }

        List<Order> result = mapNativeRowsToOrders(
                orderRepository.findByStatusAndAmountNativeRaw(status.name(), amount)
        );
        cache.put(key, result);

        return result;
    }

    public List<Order> getByCustomerName(String name, double amount) {
        OrderFilter key = new OrderFilter(null, name, amount, "customer-jpql");

        if (cache.containsKey(key)) {
            log.info(FROM_CACHE_LOG);
            return cache.get(key);
        }

        List<Order> result = orderRepository.findByCustomerNameAndAmount(name, amount);
        cache.put(key, result);

        return result;
    }

    public List<Order> getByCustomerNameNative(String name, double amount) {
        OrderFilter key = new OrderFilter(null, name, amount, "customer-native");

        if (cache.containsKey(key)) {
            log.info(FROM_CACHE_LOG);
            return cache.get(key);
        }

        List<Order> result = mapNativeRowsToOrders(
                orderRepository.findByCustomerNameAndAmountNativeRaw(name, amount)
        );
        cache.put(key, result);

        return result;
    }

    public Page<Order> getPaged(int page, int size) {
        return orderRepository.findAll(PageRequest.of(page, size));
    }

    @Transactional
    public Order createOrder(OrderCreateDto dto) {
        Order saved = orderRepository.save(buildOrder(dto));
        cache.clear();
        return saved;
    }

    @Transactional
    public List<Order> createOrdersBulkTransactional(List<OrderCreateDto> dtos) {
        return createOrdersBulk(dtos);
    }

    public List<Order> createOrdersBulkNonTransactional(List<OrderCreateDto> dtos) {
        return createOrdersBulk(dtos);
    }


    private List<Order> createOrdersBulk(List<OrderCreateDto> dtos) {
        List<OrderCreateDto> safeDtos = Optional.ofNullable(dtos)
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "orders required"));

        List<Order> savedOrders = safeDtos.stream()
                .map(this::buildOrder)
                .map(orderRepository::save)
                .collect(Collectors.toList());

        cache.clear();
        return savedOrders;
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

    private Order buildOrder(OrderCreateDto dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        List<Long> productIds = Optional.ofNullable(dto.getProductIds())
                .filter(ids -> !ids.isEmpty())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "products required"));

        Set<Product> products = productIds.stream()
                .map(productId -> productRepository.findById(productId)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Product not found: " + productId
                        )))
                .collect(Collectors.toCollection(HashSet::new));

        Order order = new Order();
        order.setCustomer(customer);
        order.setProducts(products);
        order.setStatus(Optional.ofNullable(dto.getStatus()).orElse(OrderStatus.NEW));

        double total = products.stream()
                .mapToDouble(Product::getPrice)
                .sum();
        order.setTotalAmount(total);

        return order;
    }

    private List<Order> mapNativeRowsToOrders(List<Object[]> rows) {
        Map<Long, Order> orders = new LinkedHashMap<>();
        Map<Long, Map<Long, Product>> orderProducts = new HashMap<>();

        for (Object[] row : rows) {
            Long orderId = ((Number) row[0]).longValue();
            Order order = orders.get(orderId);

            if (order == null) {
                order = new Order();
                order.setId(orderId);
                order.setStatus(OrderStatus.valueOf((String) row[1]));
                order.setTotalAmount(((Number) row[2]).doubleValue());
                order.setProducts(new HashSet<>());

                Customer customer = new Customer();
                customer.setId(((Number) row[3]).longValue());
                customer.setName((String) row[4]);
                customer.setEmail((String) row[5]);
                customer.setPhone((String) row[6]);
                order.setCustomer(customer);

                orders.put(orderId, order);
            }

            Long productId = ((Number) row[7]).longValue();
            Map<Long, Product> products = orderProducts.computeIfAbsent(orderId, id -> new LinkedHashMap<>());
            Product product = products.get(productId);

            if (product == null) {
                product = new Product();
                product.setId(productId);
                product.setName((String) row[8]);
                product.setPrice(((Number) row[9]).doubleValue());
                product.setCategories(new HashSet<>());
                products.put(productId, product);
                order.getProducts().add(product);
            }

            if (row[10] != null) {
                Long categoryId = ((Number) row[10]).longValue();
                boolean exists = product.getCategories().stream()
                        .anyMatch(category -> category.getId().equals(categoryId));

                if (!exists) {
                    Category category = new Category();
                    category.setId(categoryId);
                    category.setName((String) row[11]);
                    product.getCategories().add(category);
                }
            }
        }

        return new ArrayList<>(orders.values());
    }
}