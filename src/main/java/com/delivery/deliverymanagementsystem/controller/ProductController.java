package com.delivery.deliverymanagementsystem.controller;

import com.delivery.deliverymanagementsystem.entity.Product;
import com.delivery.deliverymanagementsystem.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public Product create(@RequestBody Product request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        return productService.create(product);
    }

    @GetMapping
    public List<Product> getAll() {
        return productService.getAll();
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return productService.getById(id);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        return productService.update(id, product);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}