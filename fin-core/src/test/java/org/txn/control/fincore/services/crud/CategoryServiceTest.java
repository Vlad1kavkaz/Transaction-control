package org.txn.control.fincore.services.crud;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.txn.control.fincore.entities.CategoryEntity;
import org.txn.control.fincore.mappers.ResponseMapper;
import org.txn.control.fincore.model.Category;
import org.txn.control.fincore.repositories.CategoryRepository;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ResponseMapper responseMapper;

    private List<CategoryEntity> categoryEntities;
    private List<Category> categories;

    @BeforeEach
    void setUp() {
        CategoryEntity categoryEntity1 = new CategoryEntity(UUID.randomUUID(), "Groceries");
        CategoryEntity categoryEntity2 = new CategoryEntity(UUID.randomUUID(), "Electronics");

        categoryEntities = List.of(categoryEntity1, categoryEntity2);

        Category category1 = new Category();
        category1.setId(categoryEntity1.getId());
        category1.setName(categoryEntity1.getName());
        Category category2 = new Category();
        category2.setId(categoryEntity2.getId());
        category2.setName(categoryEntity2.getName());
        categories = List.of(category1, category2);
    }

    @Test
    void testFindAll_Success() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(categoryEntities);
        when(responseMapper.toResponseCategory(categoryEntities.get(0))).thenReturn(categories.get(0));
        when(responseMapper.toResponseCategory(categoryEntities.get(1))).thenReturn(categories.get(1));

        // Act
        List<Category> actualCategories = categoryService.findAll();

        // Assert
        assertThat(actualCategories).isNotNull().hasSize(2);
        assertThat(actualCategories).containsExactlyInAnyOrderElementsOf(categories);

        verify(categoryRepository, times(1)).findAll();
        verify(responseMapper, times(2)).toResponseCategory(any(CategoryEntity.class));
    }

    @Test
    void testFindAll_EmptyResult() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(List.of());

        // Act
        List<Category> actualCategories = categoryService.findAll();

        // Assert
        assertThat(actualCategories).isEmpty();
        verify(categoryRepository, times(1)).findAll();
        verify(responseMapper, never()).toResponseCategory(any(CategoryEntity.class));
    }
}
