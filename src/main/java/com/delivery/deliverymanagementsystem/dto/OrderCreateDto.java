package com.delivery.deliverymanagementsystem.dto;

import java.util.List;

public class OrderCreateDto {

    private Long customerId;
    private List<Long> productIds;
    private String status;
    private String paymentMethod;

    public Long getCustomerId() { return customerId; }
    public List<Long> getProductIds() { return productIds; }
    public String getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }

    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public void setProductIds(List<Long> productIds) { this.productIds = productIds; }
    public void setStatus(String status) { this.status = status; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}