package com.delivery.deliverymanagementsystem.repository;

import com.delivery.deliverymanagementsystem.entity.Order;
import com.delivery.deliverymanagementsystem.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Override
    @EntityGraph(attributePaths = {"customer", "products"})
    List<Order> findAll();

    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.totalAmount > :amount")
    List<Order> findByStatusAndAmount(OrderStatus status, double amount);

    @Query(value = "SELECT * FROM orders WHERE status = :status AND total_amount > :amount", nativeQuery = true)
    List<Order> findByStatusAndAmountNative(String status, double amount);

    Page<Order> findAll(Pageable pageable);
}