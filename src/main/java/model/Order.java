package com.delivery.deliverymanagementsystem.model;

public class Order {

    private Long id;
    private String customerName;
    private String address;
    private String status;

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