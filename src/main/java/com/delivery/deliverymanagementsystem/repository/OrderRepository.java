package com.delivery.deliverymanagementsystem.repository;

import com.delivery.deliverymanagementsystem.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"products", "customer"})
    List<Order> findAll();
}