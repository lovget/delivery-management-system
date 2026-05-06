package com.delivery.deliverymanagementsystem.controller;

import com.delivery.deliverymanagementsystem.dto.ProductCreateDto;
import com.delivery.deliverymanagementsystem.entity.Product;
import com.delivery.deliverymanagementsystem.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Операции с товарами")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Получить список товаров")
    public List<Product> getAll() {
        return productService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить товар по id")
    public Product getById(@PathVariable Long id) {
        return productService.getById(id);
    }

    @PostMapping
    @Operation(summary = "Создать товар")
    public Product create(@Valid @RequestBody ProductCreateDto dto) {
        return productService.create(dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить товар")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}