package com.delivery.deliverymanagementsystem.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Связь с Customer
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // Связь с Product (многие ко многим)
    @ManyToMany
    @JoinTable(
            name = "order_products",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;

    // Связь с Payment
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    private Double totalAmount;

    private String status;

    // Required by JPA
    public Order() {
    }

    public Order(Customer customer, List<Product> products, Double totalAmount, String status) {
        this.customer = customer;
        this.products = products;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<Product> getProducts() {
        return products;
    }

    public Payment getPayment() {
        return payment;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}