package org.txn.control.fincore.services.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.txn.control.fincore.model.Transaction;
import org.txn.control.fincore.model.TransactionsFilterRequestDto;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerServiceTest {

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    @Mock
    private KafkaProcessingService processingService;

    @Mock
    private KafkaProducerService kafkaProducerService;

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
        when(processingService.getTransactions(filterRequest)).thenReturn(transactions);

        // Act
        kafkaConsumerService.consumeFilteredTransaction(filterRequest, key);

        // Assert
        verify(processingService).getTransactions(filterRequest);
        verify(kafkaProducerService).sendResponse(any(), eq(key), eq(transactions));
    }

    @Test
    void testConsumeFilteredTransaction_InvalidPayload() {
        // Arrange
        Object invalidPayload = new Object();

        // Act
        kafkaConsumerService.consumeFilteredTransaction(invalidPayload, key);

        // Assert
        verifyNoInteractions(processingService);
        verifyNoInteractions(kafkaProducerService);
    }
}