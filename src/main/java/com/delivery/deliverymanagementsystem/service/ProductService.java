package com.delivery.deliverymanagementsystem.service;

import com.delivery.deliverymanagementsystem.dto.ProductCreateDto;
import com.delivery.deliverymanagementsystem.entity.Category;
import com.delivery.deliverymanagementsystem.entity.Product;
import com.delivery.deliverymanagementsystem.repository.CategoryRepository;
import com.delivery.deliverymanagementsystem.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    public Product create(ProductCreateDto dto) {

        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());

        Set<Category> categories = new HashSet<>();

        if (dto.getCategoryIds() != null) {
            for (Long id : dto.getCategoryIds()) {
                Category category = categoryRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found: " + id));
                categories.add(category);
            }
        }

        product.setCategories(categories);

        return productRepository.save(product);
    }

    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }

        productRepository.deleteById(id);
    }
}