package com.delivery.deliverymanagementsystem.dto;

import com.delivery.deliverymanagementsystem.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "DTO для создания заказа")
public class OrderCreateDto {

    @Schema(description = "Идентификатор существующего клиента", example = "1")
    @NotNull(message = "Customer id is required")
    private Long customerId;

    @Schema(description = "Список идентификаторов существующих товаров", example = "[1,2]")
    @NotEmpty(message = "At least one product id is required")
    private List<Long> productIds;

    @Schema(description = "Статус заказа (если не передан, будет NEW)", example = "NEW")
    private OrderStatus status;

    public Long getCustomerId() { return customerId; }
    public List<Long> getProductIds() { return productIds; }
    public OrderStatus getStatus() { return status; }

    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public void setProductIds(List<Long> productIds) { this.productIds = productIds; }
    public void setStatus(OrderStatus status) { this.status = status; }
}