package com.delivery.deliverymanagementsystem.dto;

import java.util.List;

public class OrderCreateDto {

    private Long customerId;
    private List<Long> productIds;
    private String status;
    private String paymentMethod;

    public Long getCustomerId() {
        return customerId;
    }

    public List<Long> getProductIds() {
        return productIds;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}