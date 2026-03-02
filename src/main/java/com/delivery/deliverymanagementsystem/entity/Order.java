package com.delivery.deliverymanagementsystem.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToMany
    @JoinTable(
            name = "order_products",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    public Order() {}

    public Long getId() { return id; }

    public String getStatus() { return status; }

    public Customer getCustomer() { return customer; }

    public List<Product> getProducts() { return products; }

    public Payment getPayment() { return payment; }

    public void setStatus(String status) { this.status = status; }

    public void setCustomer(Customer customer) { this.customer = customer; }

    public void setProducts(List<Product> products) { this.products = products; }

    public void setPayment(Payment payment) { this.payment = payment; }
}