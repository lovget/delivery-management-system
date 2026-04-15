package com.delivery.deliverymanagementsystem.dto;

import com.delivery.deliverymanagementsystem.entity.OrderStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class OrderCreateDto {

    @NotNull
    private Long customerId;

    @NotEmpty
    private List<Long> productIds;

    private OrderStatus status;

    public Long getCustomerId() { return customerId; }
    public List<Long> getProductIds() { return productIds; }
    public OrderStatus getStatus() { return status; }

    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public void setProductIds(List<Long> productIds) { this.productIds = productIds; }
    public void setStatus(OrderStatus status) { this.status = status; }
}