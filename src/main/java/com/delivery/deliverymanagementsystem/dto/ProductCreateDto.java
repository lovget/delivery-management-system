package com.delivery.deliverymanagementsystem.dto;

import com.delivery.deliverymanagementsystem.entity.Category;

import java.util.List;

public class ProductCreateDto {

    private String name;
    private Double price;
    private List<Category> categories;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}