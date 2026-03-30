package com.delivery.deliverymanagementsystem.dto;

public class PaymentCreateDto {
    private String method;
    private Double amount;
    private Long orderId;

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
}