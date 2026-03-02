package com.delivery.deliverymanagementsystem.dto;

public class OrderDto {

    private Long id;
    private String customerName;
    private String status;

    public OrderDto(Long id, String customerName, String status) {
        this.id = id;
        this.customerName = customerName;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getStatus() {
        return status;
    }
}