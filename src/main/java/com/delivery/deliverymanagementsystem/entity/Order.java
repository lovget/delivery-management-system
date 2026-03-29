package com.delivery.deliverymanagementsystem.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "order_products",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private double totalAmount;

    public Long getId() { return id; }
    public Customer getCustomer() { return customer; }
    public List<Product> getProducts() { return products; }
    public OrderStatus getStatus() { return status; }
    public double getTotalAmount() { return totalAmount; }

    public void setId(Long id) { this.id = id; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public void setProducts(List<Product> products) { this.products = products; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}