package com.delivery.deliverymanagementsystem.repository;

import com.delivery.deliverymanagementsystem.model.Order;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderRepository {

    private final List<Order> orders = new ArrayList<>();

    public OrderRepository() {
        orders.add(new Order(1L, "Alex", "New York", "CREATED"));
        orders.add(new Order(2L, "John", "Chicago", "SHIPPED"));
    }

    public List<Order> findAll() {
        return orders;
    }

    public Order findById(Long id) {
        return orders.stream()
                .filter(order -> order.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}