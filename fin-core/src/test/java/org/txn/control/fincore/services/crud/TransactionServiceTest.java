package org.txn.control.fincore.services.crud;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.txn.control.fincore.entities.CategoryEntity;
import org.txn.control.fincore.entities.ExpenseEntity;
import org.txn.control.fincore.exception.ExpenseNotFoundException;
import org.txn.control.fincore.exception.IncomeNotFoundException;
import org.txn.control.fincore.exception.UserNotFoundException;
import org.txn.control.fincore.feign.clients.MsGenerateClient;
import org.txn.control.fincore.mappers.ResponseMapper;
import org.txn.control.fincore.model.Transaction;
import org.txn.control.fincore.model.TransactionUpdate;
import org.txn.control.fincore.repositories.CategoryRepository;
import org.txn.control.fincore.repositories.ExpenseRepository;
import org.txn.control.fincore.repositories.IncomeRepository;
import org.txn.control.fincore.repositories.PersonRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private MsGenerateClient client;

    @Mock
    private ResponseMapper responseMapper;

    private UUID userId;
    private UUID transactionId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        transactionId = UUID.randomUUID();
    }

    @Test
    void testGetTransactions_UserNotFound_ShouldThrowException() {
        // Arrange
        when(personRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () ->
                transactionService.getTransactions(userId, "EXPENSE", LocalDate.now(), LocalDate.now(), 0, 10));
    }

    @Test
    void testGetTransactions_ExpenseType_ShouldReturnExpenses() {
        // Arrange
        List<ExpenseEntity> expenses = List.of(new ExpenseEntity());
        when(personRepository.existsById(userId)).thenReturn(true);
        when(client.transaction(userId)).thenReturn(Collections.emptyList());
        when(expenseRepository.findFilteredExpenses(any(), any(), any(), any(), any())).thenReturn(expenses);
        when(responseMapper.expenseToTransaction(any())).thenReturn(new Transaction());

        // Act
        List<Transaction> result = transactionService.getTransactions(userId, "EXPENSE", LocalDate.now(), LocalDate.now(), 0, 10);

        // Assert
        assertFalse(result.isEmpty());
        verify(expenseRepository, times(1)).findFilteredExpenses(any(), any(), any(), any(), any());
    }

    @Test
    void testGetTransactionById_ExpenseNotFound_ShouldThrowException() {
        // Arrange
        when(expenseRepository.findById(transactionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ExpenseNotFoundException.class, () ->
                transactionService.getTransactionById(transactionId, "EXPENSE"));
    }

    @Test
    void testGetTransactionById_ExpenseFound_ShouldReturnTransaction() {
        // Arrange
        ExpenseEntity expenseEntity = new ExpenseEntity();
        when(expenseRepository.findById(transactionId)).thenReturn(Optional.of(expenseEntity));
        when(responseMapper.expenseToTransaction(expenseEntity)).thenReturn(new Transaction());

        // Act
        Transaction result = transactionService.getTransactionById(transactionId, "EXPENSE");

        // Assert
        assertNotNull(result);
    }

    @Test
    void testUpdateTransaction_ExpenseNotFound_ShouldThrowException() {
        // Arrange
        TransactionUpdate update = new TransactionUpdate();
        update.setType(TransactionUpdate.TypeEnum.EXPENSE);

        // Act
        when(expenseRepository.findById(transactionId)).thenReturn(Optional.empty());

        // Assert
        assertThrows(ExpenseNotFoundException.class, () ->
                transactionService.updateTransaction(transactionId, update));
    }

    @Test
    void testUpdateTransaction_ExpenseFound_ShouldUpdateExpense() {
        // Arrange
        TransactionUpdate update = new TransactionUpdate();
        update.setType(TransactionUpdate.TypeEnum.EXPENSE);
        update.setAmount(BigDecimal.valueOf(100.00));

        // Act
        ExpenseEntity expenseEntity = new ExpenseEntity();
        when(expenseRepository.findById(transactionId)).thenReturn(Optional.of(expenseEntity));
        when(categoryRepository.findById(any())).thenReturn(Optional.of(new CategoryEntity()));

        transactionService.updateTransaction(transactionId, update);

        // Assert
        verify(expenseRepository).save(expenseEntity);
    }

    @Test
    void testDeleteTransaction_ExpenseNotFound_ShouldThrowException() {
        // Arrange
        when(expenseRepository.existsById(transactionId)).thenReturn(false);

        // Act & Assert
        assertThrows(ExpenseNotFoundException.class, () ->
                transactionService.deleteTransaction(transactionId, "EXPENSE"));
    }

    @Test
    void testDeleteTransaction_ExpenseFound_ShouldDeleteExpense() {
        // Arrange
        when(expenseRepository.existsById(transactionId)).thenReturn(true);

        // Act
        transactionService.deleteTransaction(transactionId, "EXPENSE");

        // Assert
        verify(expenseRepository).deleteById(transactionId);
    }

    @Test
    void testUpdateTransaction_IncomeNotFound_ShouldThrowException() {
        // Arrange
        TransactionUpdate update = new TransactionUpdate();
        update.setType(TransactionUpdate.TypeEnum.INCOME);

        // Act
        when(incomeRepository.findById(transactionId)).thenReturn(Optional.empty());

        // Assert
        assertThrows(IncomeNotFoundException.class, () ->
                transactionService.updateTransaction(transactionId, update));
    }
}
