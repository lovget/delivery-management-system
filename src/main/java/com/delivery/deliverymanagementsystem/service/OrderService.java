package com.delivery.deliverymanagementsystem.service;

import com.delivery.deliverymanagementsystem.entity.*;
import com.delivery.deliverymanagementsystem.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        ProductRepository productRepository,
                        PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Order createOrder(Long customerId, List<Long> productIds, String status, String paymentMethod) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow();

        List<Product> products = productRepository.findAllById(productIds);

        double totalAmount = products.stream()
                .mapToDouble(Product::getPrice)
                .sum();

        Order order = new Order();
        order.setCustomer(customer);
        order.setProducts(products);
        order.setStatus(status);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        Payment payment = new Payment();
        payment.setOrder(savedOrder);
        payment.setAmount(totalAmount);
        payment.setMethod(paymentMethod);

        paymentRepository.save(payment);

        return savedOrder;
    }

    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    public Order getById(Long id) {
        return orderRepository.findById(id).orElseThrow();
    }

    public void delete(Long id) {
        orderRepository.deleteById(id);
    }
}