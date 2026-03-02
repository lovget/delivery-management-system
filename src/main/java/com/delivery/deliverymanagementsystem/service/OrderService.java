package com.delivery.deliverymanagementsystem.service;

import com.delivery.deliverymanagementsystem.dto.OrderDto;
import com.delivery.deliverymanagementsystem.entity.Order;
import com.delivery.deliverymanagementsystem.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
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

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public void delete(Long id) {
        orderRepository.deleteById(id);
    }
}