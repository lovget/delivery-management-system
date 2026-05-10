package com.delivery.deliverymanagementsystem.service;

import com.delivery.deliverymanagementsystem.entity.Category;
import com.delivery.deliverymanagementsystem.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void create_shouldSaveCategory() {
        Category category = new Category();
        category.setName("Fast food");

        when(categoryRepository.save(category)).thenReturn(category);

        Category result = categoryService.create(category);

        assertEquals("Fast food", result.getName());
        verify(categoryRepository).save(category);
    }

    @Test
    void getAll_shouldReturnList() {
        when(categoryRepository.findAll()).thenReturn(List.of(new Category(), new Category()));

        assertEquals(2, categoryService.getAll().size());
        verify(categoryRepository).findAll();
    }

    @Test
    void getById_shouldReturnCategory() {
        Category category = new Category();
        category.setId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertEquals(1L, categoryService.getById(1L).getId());
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> categoryService.getById(1L));
    }

    @Test
    void update_shouldUpdateAndSave() {
        Category existing = new Category();
        existing.setId(10L);
        existing.setName("Old");

        Category updated = new Category();
        updated.setName("New");

        when(categoryRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(existing);

        Category result = categoryService.update(10L, updated);

        assertEquals("New", result.getName());
        verify(categoryRepository).save(existing);
    }

    @Test
    void delete_shouldRemoveWhenExists() {
        when(categoryRepository.existsById(2L)).thenReturn(true);

        categoryService.delete(2L);

        verify(categoryRepository).deleteById(2L);
    }

    @Test
    void delete_shouldThrowWhenNotExists() {
        when(categoryRepository.existsById(2L)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> categoryService.delete(2L));
    }
}