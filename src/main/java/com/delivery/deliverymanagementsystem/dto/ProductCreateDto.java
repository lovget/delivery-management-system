package com.delivery.deliverymanagementsystem.dto;

import java.util.List;

public class ProductCreateDto {

    private String name;
    private double price;
    private List<Long> categoryIds;

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }
}