package org.txn.control.histgen.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.txn.control.histgen.model.Bank;
import org.txn.control.histgen.model.Category;
import org.txn.control.histgen.model.Transaction;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private List<Bank> banks;

    @Mock
    private List<Category> categories;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        Bank bank1 = Bank.builder().name("Bank A").build();
        Bank bank2 = Bank.builder().name("Bank B").build();
        Category category = Category.builder().name("Groceries").build();

        List<Bank> bankList = List.of(bank1, bank2);

        lenient().when(banks.size()).thenReturn(bankList.size());
        lenient().when(banks.get(0)).thenReturn(bank1);
        lenient().when(banks.get(1)).thenReturn(bank2);
        lenient().when(categories.size()).thenReturn(1);
        lenient().when(categories.get(0)).thenReturn(category);
    }

    @Test
    void getTransactionsForUserId_ShouldReturnTransactions() {
        // Act
        List<Transaction> transactions = transactionService.getTransactionsForUserId(userId);

        // Assert
        assertThat(transactions).isNotNull();
        assertThat(transactions).allMatch(t -> t.getUserId().equals(userId));
        assertThat(transactions).allMatch(t -> t.getBankName().equals("Bank A"));
    }
}
