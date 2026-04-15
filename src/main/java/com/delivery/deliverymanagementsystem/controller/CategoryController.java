package com.delivery.deliverymanagementsystem.controller;

import com.delivery.deliverymanagementsystem.dto.CategoryDto;
import com.delivery.deliverymanagementsystem.entity.Category;
import com.delivery.deliverymanagementsystem.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@Tag(name = "Categories", description = "Category management endpoints")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @Operation(summary = "Create category")
    public Category create(@Valid @RequestBody CategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        return categoryService.create(category);
    }

    @GetMapping
    @Operation(summary = "Get all categories")
    public List<Category> getAll() {
        return categoryService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by id")
    public Category getById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category")
    public Category update(@PathVariable Long id, @Valid @RequestBody CategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        return categoryService.update(id, category);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category")
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }
}