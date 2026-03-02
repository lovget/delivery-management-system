package com.delivery.deliverymanagementsystem.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentMethod;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    public Payment() {}

    public Long getId() { return id; }

    public String getPaymentMethod() { return paymentMethod; }

    public Order getOrder() { return order; }

    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public void setOrder(Order order) { this.order = order; }
}