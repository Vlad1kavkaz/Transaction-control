package org.txn.control.fincore.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.txn.control.fincore.entities.CategoryEntity;
import org.txn.control.fincore.entities.ExpenseEntity;
import org.txn.control.fincore.entities.IncomeEntity;
import org.txn.control.fincore.entities.PersonEntity;
import org.txn.control.fincore.exception.CategoryNotFoundException;
import org.txn.control.fincore.exception.UserNotFoundException;
import org.txn.control.fincore.model.Transaction;
import org.txn.control.fincore.repositories.CategoryRepository;
import org.txn.control.fincore.repositories.PersonRepository;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntityMapperTest {

    @InjectMocks
    private EntityMapper mapper = Mappers.getMapper(EntityMapper.class);

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PersonRepository personRepository;

    private UUID userId;
    private UUID categoryId;
    private PersonEntity person;
    private CategoryEntity category;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        person = new PersonEntity();
        person.setId(userId);

        category = new CategoryEntity();
        category.setId(categoryId);
    }

    @Test
    void toIncomeEntity_Success() {
        // Arrange
        Transaction transaction = createTransaction();
        when(personRepository.findById(userId)).thenReturn(Optional.of(person));

        // Act
        IncomeEntity incomeEntity = mapper.toIncomeEntity(transaction);

        // Assert
        assertThat(incomeEntity).isNotNull();
        assertThat(incomeEntity.getUser()).isEqualTo(person);
        assertThat(incomeEntity.getAmount()).isEqualTo(transaction.getAmount());
    }

    @Test
    void toExpenseEntity_Success() {
        // Arrange
        Transaction transaction = createTransaction();
        when(personRepository.findById(userId)).thenReturn(Optional.of(person));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // Act
        ExpenseEntity expenseEntity = mapper.toExpenseEntity(transaction);

        // Assert
        assertThat(expenseEntity).isNotNull();
        assertThat(expenseEntity.getUser()).isEqualTo(person);
        assertThat(expenseEntity.getCategory()).isEqualTo(category);
        assertThat(expenseEntity.getAmount()).isEqualTo(transaction.getAmount());
    }

    @Test
    void toIncomeEntity_UserNotFoundException() {
        // Arrange
        Transaction transaction = createTransaction();
        when(personRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> mapper.toIncomeEntity(transaction));
    }

    @Test
    void toExpenseEntity_CategoryNotFoundException() {
        // Arrange
        Transaction transaction = createTransaction();
        when(personRepository.findById(userId)).thenReturn(Optional.of(person));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> mapper.toExpenseEntity(transaction));
    }

    @Test
    void toExpenseEntity_UserNotFoundException() {
        // Arrange
        Transaction transaction = createTransaction();
        when(personRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> mapper.toExpenseEntity(transaction));
    }

    private Transaction createTransaction() {
        return new Transaction()
                .id(UUID.randomUUID())
                .userId(userId)
                .categoryId(categoryId)
                .amount(BigDecimal.valueOf(100.00))
                .date(ZonedDateTime.now())
                .description("Test Transaction");
    }
}
