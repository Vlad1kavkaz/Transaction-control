package org.txn.control.fincore.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.txn.control.fincore.BaseRepositoryTest;
import org.txn.control.fincore.entities.BankEntity;
import org.txn.control.fincore.entities.CategoryEntity;
import org.txn.control.fincore.entities.ExpenseEntity;
import org.txn.control.fincore.entities.PersonEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ExpenseRepositoryTest extends BaseRepositoryTest<ExpenseEntity> {

    private final ExpenseRepository repository;

    @Autowired
    public ExpenseRepositoryTest(TestEntityManager entityManager, ExpenseRepository repository) {
        super(entityManager);
        this.repository = repository;
    }

    @Override
    protected ExpenseEntity createTestEntity() {
        ExpenseEntity expenseEntity = new ExpenseEntity();
        expenseEntity.setDescription("Some description");
        expenseEntity.setAmount(BigDecimal.valueOf(12345.55));
        expenseEntity.setDate(ZonedDateTime.now());
        expenseEntity.setUser(createUser());
        expenseEntity.setCategory(createCategory());
        expenseEntity.setBank(createBank());
        return expenseEntity;
    }

    private PersonEntity createUser() {
        PersonEntity user = new PersonEntity();
        user.setCreatedAt(ZonedDateTime.now());
        user.setEmail("qwerty@example.com");
        user.setPassword("pswd1234");
        user.setUsername("Vlad1kavkaz");
        return entityManager.persistFlushFind(user);
    }

    private CategoryEntity createCategory() {
        CategoryEntity category = new CategoryEntity();
        category.setName("Food");
        return entityManager.persistFlushFind(category);
    }

    private BankEntity createBank() {
        BankEntity bank = new BankEntity();
        bank.setCountry("Russia");
        bank.setName("SBRF");
        return entityManager.persistFlushFind(bank);
    }

    @Test
    void testSaveEntity() {
        // Arrange
        ExpenseEntity entityToSave = testEntity;

        // Act
        ExpenseEntity savedEntity = repository.save(entityToSave);

        // Assert
        assertEntity(entityToSave, savedEntity);
    }

    @Test
    void testFindById() {
        // Arrange
        ExpenseEntity savedEntity = entityManager.persistFlushFind(testEntity);

        // Act
        Optional<ExpenseEntity> foundEntity = repository.findById(savedEntity.getId());

        // Assert
        assertThat(foundEntity).isPresent();
        assertEntity(savedEntity, foundEntity.get());
    }

    @Test
    void testFindFilteredExpenses_NoResults() {
        // Arrange
        ExpenseEntity expense1 = new ExpenseEntity(
                null,
                testEntity.getUser(),
                BigDecimal.valueOf(100.00),
                testEntity.getCategory(),
                testEntity.getBank(),
                ZonedDateTime.now().minusDays(20),
                "Old Lunch");
        entityManager.persistFlushFind(expense1);

        LocalDate startDate = LocalDate.now().plusDays(10);
        LocalDate endDate = LocalDate.now().plusDays(15);

        // Act
        List<ExpenseEntity> expenses = repository.findFilteredExpenses(
                testEntity.getUser().getId(),
                startDate,
                endDate,
                0,
                10
        );

        // Assert
        assertThat(expenses).isEmpty();
    }

    private void assertEntity(ExpenseEntity expected, ExpenseEntity actual) {
        assertThat(expected.getAmount()).isEqualTo(actual.getAmount());
        assertThat(expected.getUser()).isEqualTo(actual.getUser());
        assertThat(expected.getCategory()).isEqualTo(actual.getCategory());
        assertThat(expected.getDate()).isEqualTo(actual.getDate());
        assertThat(expected.getBank()).isEqualTo(actual.getBank());
        assertThat(expected.getDescription()).isEqualTo(actual.getDescription());
    }
}
