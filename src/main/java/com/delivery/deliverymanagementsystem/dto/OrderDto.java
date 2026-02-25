package com.delivery.deliverymanagementsystem.dto;

public record OrderDto(
        Long id,
        String customerName,
        String status
) {
}