package com.delivery.deliverymanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
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

    private String status;

    private double totalAmount;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    public Long getId() { return id; }
    public Customer getCustomer() { return customer; }
    public List<Product> getProducts() { return products; }
    public String getStatus() { return status; }
    public double getTotalAmount() { return totalAmount; }
    public Payment getPayment() { return payment; }

    public void setCustomer(Customer customer) { this.customer = customer; }
    public void setProducts(List<Product> products) { this.products = products; }
    public void setStatus(String status) { this.status = status; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}