package org.txn.control.histgen.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.txn.control.histgen.model.Transaction;
import org.txn.control.histgen.services.TransactionService;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private MockMvc mockMvc;

    private UUID userId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
        userId = UUID.randomUUID();
    }

    @Test
    void getTransactions_ReturnsListOfTransactions() throws Exception {
        // Arrange
        List<Transaction> transactions = Arrays.asList(
                Transaction.builder()
                        .userId(userId)
                        .bankName("Bank A")
                        .type(Transaction.TypeEnum.EXPENSE)
                        .categoryName("Groceries")
                        .build(),
                Transaction.builder()
                        .userId(userId)
                        .bankName("Bank B")
                        .type(Transaction.TypeEnum.INCOME)
                        .categoryName(null)
                        .build());

        when(transactionService.getTransactionsForUserId(userId)).thenReturn(transactions);

        // Act & Assert
        mockMvc.perform(get("/v1/api/transaction/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].bankName").value("Bank A"))
                .andExpect(jsonPath("$[0].type").value("EXPENSE"))
                .andExpect(jsonPath("$[1].type").value("INCOME"));
    }
}
