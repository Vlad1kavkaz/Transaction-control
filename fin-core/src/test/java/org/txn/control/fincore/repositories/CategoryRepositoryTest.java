package org.txn.control.fincore.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.txn.control.fincore.BaseRepositoryTest;
import org.txn.control.fincore.entities.CategoryEntity;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CategoryRepositoryTest extends BaseRepositoryTest<CategoryEntity> {

    private final CategoryRepository repository;

    @Autowired
    public CategoryRepositoryTest(TestEntityManager entityManager, CategoryRepository repository) {
        super(entityManager);
        this.repository = repository;
    }

    @Override
    protected CategoryEntity createTestEntity() {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName("Electronics");
        return categoryEntity;
    }

    @Test
    void testSaveCategoryEntity() {
        // Arrange
        CategoryEntity categoryToSave = testEntity;

        // Act
        CategoryEntity savedCategory = repository.save(categoryToSave);

        // Assert
        assertThat(savedCategory).isNotNull();
        assertThat(savedCategory.getId()).isNotNull();
        assertThat(savedCategory.getName()).isEqualTo(testEntity.getName());
    }

    @Test
    void testFindById_ExistingCategory() {
        // Arrange
        CategoryEntity savedCategory = entityManager.persistFlushFind(testEntity);

        // Act
        Optional<CategoryEntity> foundCategory = repository.findById(savedCategory.getId());

        // Assert
        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getName()).isEqualTo(savedCategory.getName());
    }

    @Test
    void testFindById_NonExistingCategory() {
        // Act
        Optional<CategoryEntity> foundCategory = repository.findById(UUID.randomUUID());

        // Assert
        assertThat(foundCategory).isNotPresent();
    }

    @Test
    void testDeleteCategoryEntity() {
        // Arrange
        CategoryEntity savedCategory = entityManager.persistFlushFind(testEntity);

        // Act
        repository.deleteById(savedCategory.getId());
        Optional<CategoryEntity> deletedCategory = repository.findById(savedCategory.getId());

        // Assert
        assertThat(deletedCategory).isNotPresent();
    }

    @Test
    void testUpdateCategoryEntity() {
        // Arrange
        CategoryEntity savedCategory = entityManager.persistFlushFind(testEntity);

        // Act
        savedCategory.setName("Outdoor Sports");
        CategoryEntity updatedCategory = repository.save(savedCategory);

        // Assert
        assertThat(updatedCategory.getName()).isEqualTo("Outdoor Sports");
    }
}
