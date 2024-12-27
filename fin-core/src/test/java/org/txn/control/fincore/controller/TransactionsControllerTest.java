package org.txn.control.fincore.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.txn.control.fincore.model.Transaction;
import org.txn.control.fincore.model.TransactionUpdate;
import org.txn.control.fincore.services.crud.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionsControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionsController transactionsController;

    private UUID userId;
    private UUID transactionId;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        transactionId = UUID.randomUUID();
        transaction = new Transaction()
                .id(transactionId)
                .amount(BigDecimal.valueOf(1000.00))
                .type(Transaction.TypeEnum.valueOf("EXPENSE"));
    }

    @Test
    void testGetTransactions() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now();
        List<Transaction> transactions = List.of(transaction);
        when(transactionService.getTransactions(userId, "EXPENSE", startDate, endDate, 0, 10))
                .thenReturn(transactions);

        // Act
        ResponseEntity<List<Transaction>> response = transactionsController.transactionsGet(
                userId, "EXPENSE", startDate, endDate, 0, 10);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertEquals(transactions, response.getBody());
    }

    @Test
    void testDeleteTransaction() {
        // Act
        ResponseEntity<Void> response = transactionsController.transactionsIdDelete(transactionId);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        verify(transactionService, times(1)).deleteTransaction(transactionId, "EXPENSE");
    }

    @Test
    void testGetTransactionById() {
        // Arrange
        when(transactionService.getTransactionById(transactionId, "EXPENSE"))
                .thenReturn(transaction);

        // Act
        ResponseEntity<Transaction> response = transactionsController.transactionsIdGet(transactionId);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertEquals(transaction, response.getBody());
    }

    @Test
    void testUpdateTransaction() {
        // Arrange
        TransactionUpdate transactionUpdate = new TransactionUpdate().amount(BigDecimal.valueOf(1500.00));
        doNothing().when(transactionService).updateTransaction(transactionId, transactionUpdate);

        // Act
        ResponseEntity<Void> response = transactionsController.transactionsIdPut(transactionId, transactionUpdate);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        verify(transactionService, times(1)).updateTransaction(transactionId, transactionUpdate);
    }
}
