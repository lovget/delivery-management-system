package com.delivery.deliverymanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private double price;

    @ManyToMany(mappedBy = "products")
    @JsonIgnore
    private List<Order> orders;

    public Long getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public List<Order> getOrders() { return orders; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setOrders(List<Order> orders) { this.orders = orders; }
}