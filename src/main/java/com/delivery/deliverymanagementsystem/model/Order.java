package com.delivery.deliverymanagementsystem.model;

public class Order {

    private final Long id;
    private final String customerName;
    private final String address;
    private final String status;

    public Order(Long id, String customerName, String address, String status) {
        this.id = id;
        this.customerName = customerName;
        this.address = address;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getAddress() {
        return address;
    }

    public String getStatus() {
        return status;
    }
}