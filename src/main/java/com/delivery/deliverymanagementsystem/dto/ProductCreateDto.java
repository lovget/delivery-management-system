package com.delivery.deliverymanagementsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.List;

@Schema(description = "DTO для создания товара")
public class ProductCreateDto {

    @Schema(description = "Название товара", example = "Cheeseburger")
    @NotBlank(message = "Название товара must not be blank")
    private String name;

    @Schema(description = "Цена товара", example = "250")
    @Positive(message = "Price must be greater than 0")
    private double price;

    @Schema(description = "Список идентификаторов существующих категорий", example = "[1,2]")
    private List<Long> categoryIds;

    public String getName() { return name; }
    public double getPrice() { return price; }
    public List<Long> getCategoryIds() { return categoryIds; }

    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setCategoryIds(List<Long> categoryIds) { this.categoryIds = categoryIds; }
}