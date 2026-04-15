package com.delivery.deliverymanagementsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "DTO for payment creation")
public class PaymentCreateDto {

    @Schema(description = "Payment method", example = "CARD")
    @NotBlank(message = "Payment method must not be blank")
    private String method;

    @Schema(description = "Payment amount", example = "499.99")
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private Double amount;

    @Schema(description = "Existing order id", example = "1")
    @NotNull(message = "Order id is required")
    private Long orderId;

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
}