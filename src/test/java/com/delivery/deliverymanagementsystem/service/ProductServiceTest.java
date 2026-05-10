package com.delivery.deliverymanagementsystem.service;

import com.delivery.deliverymanagementsystem.dto.ProductCreateDto;
import com.delivery.deliverymanagementsystem.entity.Category;
import com.delivery.deliverymanagementsystem.entity.Product;
import com.delivery.deliverymanagementsystem.repository.CategoryRepository;
import com.delivery.deliverymanagementsystem.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void getAll_shouldReturnProducts() {
        when(productRepository.findAll()).thenReturn(List.of(new Product(), new Product()));

        assertEquals(2, productService.getAll().size());
    }

    @Test
    void getById_shouldReturnProduct() {
        Product product = new Product();
        product.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertEquals(1L, productService.getById(1L).getId());
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> productService.getById(1L));
    }

    @Test
    void create_shouldSaveWithCategories() {
        ProductCreateDto dto = new ProductCreateDto();
        dto.setName("Burger");
        dto.setPrice(12.5);
        dto.setCategoryIds(List.of(1L, 2L));

        Category c1 = new Category();
        c1.setId(1L);
        Category c2 = new Category();
        c2.setId(2L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(c1));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(c2));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.create(dto);

        assertEquals("Burger", result.getName());
        assertEquals(12.5, result.getPrice());
        assertEquals(2, result.getCategories().size());
    }

    @Test
    void create_shouldSaveWithoutCategoriesWhenNull() {
        ProductCreateDto dto = new ProductCreateDto();
        dto.setName("Cola");
        dto.setPrice(3.0);
        dto.setCategoryIds(null);

        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.create(dto);

        assertEquals(0, result.getCategories().size());
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertEquals("Cola", captor.getValue().getName());
    }

    @Test
    void create_shouldThrowWhenCategoryMissing() {
        ProductCreateDto dto = new ProductCreateDto();
        dto.setName("Fries");
        dto.setPrice(5.0);
        dto.setCategoryIds(List.of(999L));

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> productService.create(dto));
    }

    @Test
    void delete_shouldDeleteWhenExists() {
        when(productRepository.existsById(3L)).thenReturn(true);

        productService.delete(3L);

        verify(productRepository).deleteById(3L);
    }

    @Test
    void delete_shouldThrowWhenNotExists() {
        when(productRepository.existsById(3L)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> productService.delete(3L));
    }
}