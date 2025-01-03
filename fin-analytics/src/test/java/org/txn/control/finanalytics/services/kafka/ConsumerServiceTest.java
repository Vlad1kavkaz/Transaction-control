package org.txn.control.finanalytics.services.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.txn.control.finanalytics.model.TransactionsFilterRequestDto;
import org.txn.control.finanalytics.services.AnalyticsUtils;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConsumerServiceTest {

    @InjectMocks
    private ConsumerService kafkaConsumerService;

    @Mock
    private AnalyticsUtils analyticsUtils;

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

        // Act
        kafkaConsumerService.consumeTransactions(filterRequest, key);

        // Assert
        verify(analyticsUtils).completeRequest(any(), any());
    }
}
