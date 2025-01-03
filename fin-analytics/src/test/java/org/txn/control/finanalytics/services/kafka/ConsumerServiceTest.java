package org.txn.control.finanalytics.services.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.txn.control.finanalytics.model.Transaction;
import org.txn.control.finanalytics.model.TransactionsFilterRequestDto;
import org.txn.control.finanalytics.services.AnalyticsService;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConsumerServiceTest {

    @InjectMocks
    private ConsumerService kafkaConsumerService;

    @Mock
    private AnalyticsService analyticsService;

    private String key;

    @BeforeEach
    void setUp() {
        key = UUID.randomUUID().toString();
    }

    @Test
    void testConsumeFilteredTransaction_ValidPayload() {
        // Arrange
        TransactionsFilterRequestDto filterRequest = new TransactionsFilterRequestDto();
        filterRequest.setUserId(UUID.randomUUID());
        filterRequest.setType(TransactionsFilterRequestDto.TypeEnum.EXPENSE);
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setUserId(UUID.randomUUID());
        List<Transaction> transactions = List.of(transaction);

        // Act
        kafkaConsumerService.consumeTransactions(filterRequest, key);

        // Assert
        verify(analyticsService).completeRequest(any(), any());
    }
}
