package com.delivery.deliverymanagementsystem.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders;

    // 🔹 ОБЯЗАТЕЛЬНО (для Spring)
    public Customer() {
    }

    // 🔹 Конструктор (необязательно, но удобно)
    public Customer(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // 🔹 Геттеры
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public List<Order> getOrders() {
        return orders;
    }

    // 🔹 Сеттеры (КРИТИЧНО для JSON)
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}