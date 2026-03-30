package com.delivery.deliverymanagementsystem.service;

import com.delivery.deliverymanagementsystem.entity.Category;
import com.delivery.deliverymanagementsystem.entity.Product;
import com.delivery.deliverymanagementsystem.repository.CategoryRepository;
import com.delivery.deliverymanagementsystem.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Product create(Product product) {

        if (product.getCategories() != null) {
            List<Category> resultCategories = new ArrayList<>();

            for (Category c : product.getCategories()) {

                Category category = categoryRepository.findByName(c.getName())
                        .orElseGet(() -> {
                            Category newCategory = new Category();
                            newCategory.setName(c.getName());
                            return categoryRepository.save(newCategory);
                        });

                resultCategories.add(category);
            }

            product.setCategories(resultCategories);
        }

        return productRepository.save(product);
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product update(Long id, Product updated) {
        Product product = getById(id);
        product.setName(updated.getName());
        product.setPrice(updated.getPrice());
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}