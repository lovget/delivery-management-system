package com.delivery.deliverymanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private double price;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "product_categories",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    @ManyToMany(mappedBy = "products")
    @JsonIgnore
    private Set<Order> orders = new HashSet<>();

    public Long getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public Set<Category> getCategories() { return categories; }
    public Set<Order> getOrders() { return orders; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setCategories(Set<Category> categories) { this.categories = categories; }
    public void setOrders(Set<Order> orders) { this.orders = orders; }
}