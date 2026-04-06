package com.delivery.deliverymanagementsystem.repository;

import com.delivery.deliverymanagementsystem.entity.Order;
import com.delivery.deliverymanagementsystem.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Override
    @EntityGraph(attributePaths = {"customer", "products"})
    List<Order> findAll();

    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.totalAmount > :amount")
    List<Order> findByStatusAndAmount(@Param("status") OrderStatus status,
                                      @Param("amount") double amount);

    @Query(value = "SELECT * FROM orders WHERE status = :status AND total_amount > :amount", nativeQuery = true)
    List<Order> findByStatusAndAmountNative(@Param("status") String status,
                                            @Param("amount") double amount);

    @Query("SELECT o FROM Order o WHERE o.customer.name = :name AND o.totalAmount > :amount")
    List<Order> findByCustomerNameAndAmount(@Param("name") String name,
                                            @Param("amount") double amount);

    @Query(value = "SELECT o.* FROM orders o " +
            "JOIN customers c ON o.customer_id = c.id " +
            "WHERE c.name = :name AND o.total_amount > :amount",
            nativeQuery = true)
    List<Order> findByCustomerNameAndAmountNative(@Param("name") String name,
                                                  @Param("amount") double amount);

    Page<Order> findAll(Pageable pageable);
}