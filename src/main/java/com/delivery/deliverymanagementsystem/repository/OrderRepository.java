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
    @EntityGraph(attributePaths = {"customer", "products", "products.categories"})
    List<Order> findAll();

    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN FETCH o.customer c " +
            "JOIN FETCH o.products p " +
            "LEFT JOIN FETCH p.categories " +
            "WHERE o.status = :status AND o.totalAmount > :amount")
    List<Order> findByStatusAndAmount(@Param("status") OrderStatus status,
                                      @Param("amount") double amount);

    @Query(value = "SELECT o.id, o.status, o.total_amount, " +
            "c.id, c.name, c.email, c.phone, " +
            "p.id, p.name, p.price, " +
            "ca.id, ca.name " +
            "FROM orders o " +
            "JOIN customers c ON c.id = o.customer_id " +
            "JOIN order_products op ON o.id = op.order_id " +
            "JOIN products p ON p.id = op.product_id " +
            "LEFT JOIN product_categories pc ON pc.product_id = p.id " +
            "LEFT JOIN categories ca ON ca.id = pc.category_id " +
            "WHERE o.status = :status AND o.total_amount > :amount", nativeQuery = true)
    List<Object[]> findByStatusAndAmountNativeRaw(@Param("status") String status,
                                                  @Param("amount") double amount);

    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN FETCH o.customer c " +
            "JOIN FETCH o.products p " +
            "LEFT JOIN FETCH p.categories " +
            "WHERE c.name = :name AND o.totalAmount > :amount")
    List<Order> findByCustomerNameAndAmount(@Param("name") String name,
                                            @Param("amount") double amount);

    @Query(value = "SELECT o.id, o.status, o.total_amount, " +
            "c.id, c.name, c.email, c.phone, " +
            "p.id, p.name, p.price, " +
            "ca.id, ca.name " +
            "FROM orders o " +
            "JOIN customers c ON o.customer_id = c.id " +
            "JOIN order_products op ON o.id = op.order_id " +
            "JOIN products p ON p.id = op.product_id " +
            "LEFT JOIN product_categories pc ON pc.product_id = p.id " +
            "LEFT JOIN categories ca ON ca.id = pc.category_id " +
            "WHERE c.name = :name AND o.total_amount > :amount",
            nativeQuery = true)
    List<Object[]> findByCustomerNameAndAmountNativeRaw(@Param("name") String name,
                                                        @Param("amount") double amount);

    Page<Order> findAll(Pageable pageable);
}