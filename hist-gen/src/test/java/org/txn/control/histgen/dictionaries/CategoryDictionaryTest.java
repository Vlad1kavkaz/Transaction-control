package org.txn.control.histgen.dictionaries;

import org.junit.jupiter.api.Test;
import org.txn.control.histgen.model.Category;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CategoryDictionaryTest {

    @Test
    void categoryDictionary_ShouldContainExpectedCategories() {
        // Act
        List<Category> categories = CategoryDictionary.CATEGORIES;

        // Assert
        assertThat(categories).isNotEmpty();
        assertThat(categories).hasSize(4);

        assertThat(categories).extracting(Category::getName)
                .containsExactly("Еда", "Транспорт", "Подписки", "Развлечения");
    }

    @Test
    void utilityClass_ShouldThrowException_WhenInstantiated() {
        assertThrows(IllegalAccessException.class, () -> {
            CategoryDictionary categoryDictionary = CategoryDictionary.class.getDeclaredConstructor().newInstance();
        });
    }
}
