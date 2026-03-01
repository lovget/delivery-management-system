package com.delivery.deliverymanagementsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String method;

    private Double amount;

    private LocalDateTime paidAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    public Payment() {
    }

    public Payment(String method, Double amount, LocalDateTime paidAt) {
        this.method = method;
        this.amount = amount;
        this.paidAt = paidAt;
    }

    public Long getId() {
        return id;
    }

    public String getMethod() {
        return method;
    }

    public Double getAmount() {
        return amount;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public Order getOrder() {
        return order;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}