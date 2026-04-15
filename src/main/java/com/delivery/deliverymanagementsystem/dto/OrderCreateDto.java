package com.delivery.deliverymanagementsystem.dto;

import com.delivery.deliverymanagementsystem.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "DTO for order creation")
public class OrderCreateDto {

    @Schema(description = "Existing customer id", example = "1")
    @NotNull(message = "Customer id is required")
    private Long customerId;

    @Schema(description = "List of existing product ids", example = "[1,2]")
    @NotEmpty(message = "At least one product id is required")
    private List<Long> productIds;

    @Schema(description = "Order status. If omitted, NEW is used", example = "NEW")
    private OrderStatus status;

    public Long getCustomerId() { return customerId; }
    public List<Long> getProductIds() { return productIds; }
    public OrderStatus getStatus() { return status; }

    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public void setProductIds(List<Long> productIds) { this.productIds = productIds; }
    public void setStatus(OrderStatus status) { this.status = status; }
}