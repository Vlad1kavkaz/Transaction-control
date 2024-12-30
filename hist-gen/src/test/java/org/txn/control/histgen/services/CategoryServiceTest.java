package org.txn.control.histgen.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.txn.control.histgen.model.Category;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private List<Category> sendCategories;

    @Test
    void getAllCategories_ShouldReturnCategoriesAndClearQueue() {
        // Arrange
        List<Category> initialCategories = new ArrayList<>(categoryService.getAllCategories());
        when(sendCategories.addAll(initialCategories)).thenReturn(true);

        // Act
        List<Category> result = categoryService.getAllCategories();

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void scheduled_ShouldAddCategoriesToQueue() throws Exception {
        // Act
        Method scheduledMethod = CategoryService.class.getDeclaredMethod("scheduled");
        scheduledMethod.setAccessible(true);
        scheduledMethod.invoke(categoryService);

        List<Category> result = categoryService.getAllCategories();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Еда");
    }
}
