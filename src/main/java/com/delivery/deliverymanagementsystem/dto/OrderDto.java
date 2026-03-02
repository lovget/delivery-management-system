package com.delivery.deliverymanagementsystem.dto;

public class OrderDto {

    private Long id;
    private String customerName;
    private String status;
    private Double totalAmount;

    public OrderDto(Long id, String customerName, String status, Double totalAmount) {
        this.id = id;
        this.customerName = customerName;
        this.status = status;
        this.totalAmount = totalAmount;
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

    public Double getTotalAmount() {
        return totalAmount;
    }
}