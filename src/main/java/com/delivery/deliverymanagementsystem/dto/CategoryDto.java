package com.delivery.deliverymanagementsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO for category create/update requests")
public class CategoryDto {

    @Schema(description = "Category name", example = "Fast Food")
    @NotBlank(message = "Category name must not be blank")
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}