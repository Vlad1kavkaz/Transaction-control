package org.txn.control.fincore.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.txn.control.fincore.entities.BankEntity;
import org.txn.control.fincore.entities.CategoryEntity;
import org.txn.control.fincore.entities.ExpenseEntity;
import org.txn.control.fincore.entities.IncomeEntity;
import org.txn.control.fincore.model.Bank;
import org.txn.control.fincore.model.Category;
import org.txn.control.fincore.model.Transaction;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseMapperTest {

    private ResponseMapper responseMapper;

    @BeforeEach
    void setUp() {
        responseMapper = Mappers.getMapper(ResponseMapper.class);
    }

    @Test
    void testExpenseToTransaction() {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        CategoryEntity categoryEntity = new CategoryEntity(categoryId, "Groceries");

        ExpenseEntity expenseEntity = new ExpenseEntity();
        expenseEntity.setId(UUID.randomUUID());
        expenseEntity.setAmount(BigDecimal.valueOf(100.50));
        expenseEntity.setDate(ZonedDateTime.now());
        expenseEntity.setDescription("Grocery shopping");
        expenseEntity.setCategory(categoryEntity);

        // Act
        Transaction transaction = responseMapper.expenseToTransaction(expenseEntity);

        // Assert
        assertThat(transaction).isNotNull();
        assertThat(transaction.getId()).isEqualTo(expenseEntity.getId());
        assertThat(transaction.getAmount()).isEqualTo(expenseEntity.getAmount());
        assertThat(transaction.getDate()).isEqualTo(expenseEntity.getDate());
        assertThat(transaction.getDescription()).isEqualTo(expenseEntity.getDescription());
        assertThat(transaction.getType()).isEqualTo(Transaction.TypeEnum.EXPENSE);
        assertThat(transaction.getCategoryName()).isEqualTo(categoryEntity.getName());
    }

    @Test
    void testExpenseToTransaction_WithNullCategory() {
        // Arrange
        ExpenseEntity expenseEntity = new ExpenseEntity();
        expenseEntity.setId(UUID.randomUUID());
        expenseEntity.setAmount(BigDecimal.valueOf(200.00));
        expenseEntity.setDate(ZonedDateTime.now());
        expenseEntity.setDescription("Electronics");
        expenseEntity.setCategory(null);

        // Act
        Transaction transaction = responseMapper.expenseToTransaction(expenseEntity);

        // Assert
        assertThat(transaction).isNotNull();
        assertThat(transaction.getCategoryName()).isNull();
    }

    @Test
    void testIncomeToTransaction() {
        // Arrange
        IncomeEntity incomeEntity = new IncomeEntity();
        incomeEntity.setId(UUID.randomUUID());
        incomeEntity.setAmount(BigDecimal.valueOf(500.00));
        incomeEntity.setDate(ZonedDateTime.now());
        incomeEntity.setDescription("Salary");

        // Act
        Transaction transaction = responseMapper.incomeToTransaction(incomeEntity);

        // Assert
        assertThat(transaction).isNotNull();
        assertThat(transaction.getId()).isEqualTo(incomeEntity.getId());
        assertThat(transaction.getAmount()).isEqualTo(incomeEntity.getAmount());
        assertThat(transaction.getDate()).isEqualTo(incomeEntity.getDate());
        assertThat(transaction.getDescription()).isEqualTo(incomeEntity.getDescription());
        assertThat(transaction.getType()).isEqualTo(Transaction.TypeEnum.INCOME);
    }

    @Test
    void testToResponseCategory() {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        CategoryEntity categoryEntity = new CategoryEntity(categoryId, "Travel");

        // Act
        Category category = responseMapper.toResponseCategory(categoryEntity);

        // Assert
        assertThat(category).isNotNull();
        assertThat(category.getId()).isEqualTo(categoryEntity.getId());
        assertThat(category.getName()).isEqualTo(categoryEntity.getName());
    }

    @Test
    void testToResponseBank() {
        // Arrange
        UUID bankId = UUID.randomUUID();
        BankEntity bankEntity = new BankEntity(bankId, "Alpha Bank", "Russia");

        // Act
        Bank bank = responseMapper.toResponseBank(bankEntity);

        // Assert
        assertThat(bank).isNotNull();
        assertThat(bank.getId()).isEqualTo(bankEntity.getId());
        assertThat(bank.getName()).isEqualTo(bankEntity.getName());
        assertThat(bank.getCountry()).isEqualTo(bankEntity.getCountry());
    }

    @Test
    void testToResponseCategory_WithNullEntity() {
        // Act
        Category category = responseMapper.toResponseCategory(null);

        // Assert
        assertThat(category).isNull();
    }

    @Test
    void testToResponseBank_WithNullEntity() {
        // Act
        Bank bank = responseMapper.toResponseBank(null);

        // Assert
        assertThat(bank).isNull();
    }
}
