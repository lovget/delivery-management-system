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
@Tag(name = "Products", description = "Product management endpoints")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Get all products")
    public List<Product> getAll() {
        return productService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by id")
    public Product getById(@PathVariable Long id) {
        return productService.getById(id);
    }

    @PostMapping
    @Operation(summary = "Create product")
    public Product create(@Valid @RequestBody ProductCreateDto dto) {
        return productService.create(dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}