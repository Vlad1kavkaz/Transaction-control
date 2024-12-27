package org.txn.control.fincore.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.txn.control.fincore.model.Category;
import org.txn.control.fincore.services.crud.CategoryService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriesControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoriesController categoriesController;

    private List<Category> categoryList;

    @BeforeEach
    void setUp() {
        Category category1 = new Category().id(UUID.randomUUID()).name("Food");
        Category category2 = new Category().id(UUID.randomUUID()).name("Entertainment");
        categoryList = List.of(category1, category2);
    }

    @Test
    void testCategoriesGet_ReturnsCategoryList() {
        // Arrange
        when(categoryService.findAll()).thenReturn(categoryList);

        // Act
        ResponseEntity<List<Category>> response = categoriesController.categoriesGet();

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertEquals(categoryList, response.getBody());
    }

    @Test
    void testCategoriesGet_EmptyList() {
        // Arrange
        when(categoryService.findAll()).thenReturn(List.of());

        // Act
        ResponseEntity<List<Category>> response = categoriesController.categoriesGet();

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertEquals(0, Objects.requireNonNull(response.getBody()).size());
    }
}
