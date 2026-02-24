package com.delivery.deliverymanagementsystem.service;

import com.delivery.deliverymanagementsystem.dto.OrderDto;
import com.delivery.deliverymanagementsystem.model.Order;
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
                        order.getCustomerName(),
                        order.getStatus()
                ))
                .toList();
    }

    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id);

        if (order == null) {
            return null;
        }

        return new OrderDto(
                order.getId(),
                order.getCustomerName(),
                order.getStatus()
        );
    }

    public List<OrderDto> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status)
                .stream()
                .map(order -> new OrderDto(
                        order.getId(),
                        order.getCustomerName(),
                        order.getStatus()
                ))
                .toList();
    }
}