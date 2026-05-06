package com.delivery.deliverymanagementsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO для создания/обновления категории")
public class CategoryDto {

    @Schema(description = "Название категории", example = "Fast Food")
    @NotBlank(message = "Название категории must not be blank")
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}