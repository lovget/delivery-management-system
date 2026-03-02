package com.delivery.deliverymanagementsystem.service;

import com.delivery.deliverymanagementsystem.dto.OrderDto;
import com.delivery.deliverymanagementsystem.entity.*;
import com.delivery.deliverymanagementsystem.repository.*;

import org.springframework.stereotype.Service;

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

    public Order createOrder(Long customerId,
                             List<Long> productIds,
                             String status,
                             String paymentMethod) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

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

    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(order -> new OrderDto(
                        order.getId(),
                        order.getCustomer().getName(),
                        order.getStatus(),
                        order.getTotalAmount()
                ))
                .toList();
    }

    public List<OrderDto> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status)
                .stream()
                .map(order -> new OrderDto(
                        order.getId(),
                        order.getCustomer().getName(),
                        order.getStatus(),
                        order.getTotalAmount()
                ))
                .toList();
    }

    public void delete(Long id) {
        orderRepository.deleteById(id);
    }
}