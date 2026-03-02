package com.delivery.deliverymanagementsystem.service;

import com.delivery.deliverymanagementsystem.dto.OrderCreateDto;
import com.delivery.deliverymanagementsystem.dto.OrderDto;
import com.delivery.deliverymanagementsystem.entity.*;
import com.delivery.deliverymanagementsystem.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(order -> new OrderDto(
                        order.getId(),
                        order.getCustomer() != null ? order.getCustomer().getName() : null,
                        order.getStatus()
                ))
                .toList();
    }

    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return new OrderDto(
                order.getId(),
                order.getCustomer() != null ? order.getCustomer().getName() : null,
                order.getStatus()
        );
    }

    public List<OrderDto> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status)
                .stream()
                .map(order -> new OrderDto(
                        order.getId(),
                        order.getCustomer() != null ? order.getCustomer().getName() : null,
                        order.getStatus()
                ))
                .toList();
    }

    @Transactional
    public Order createOrder(OrderCreateDto dto) {

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<Product> products = productRepository.findAllById(dto.getProductIds());

        Double totalAmount = products.stream()
                .mapToDouble(Product::getPrice)
                .sum();

        Order order = new Order();
        order.setCustomer(customer);
        order.setProducts(products);
        order.setTotalAmount(totalAmount);
        order.setStatus(dto.getStatus());

        Order savedOrder = orderRepository.save(order);

        Payment payment = new Payment();
        payment.setMethod(dto.getPaymentMethod());
        payment.setAmount(totalAmount);
        payment.setPaidAt(LocalDateTime.now());
        payment.setOrder(savedOrder);

        paymentRepository.save(payment);

        return savedOrder;
    }

    public void delete(Long id) {
        orderRepository.deleteById(id);
    }
}