package com.delivery.deliverymanagementsystem.dto;

import com.delivery.deliverymanagementsystem.entity.OrderStatus;
import java.util.List;

public class OrderCreateDto {

    private Long customerId;
    private List<Long> productIds;
    private OrderStatus status;
    private String paymentMethod;

    public Long getCustomerId() {
        return customerId;
    }

    public List<Long> getProductIds() {
        return productIds;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}