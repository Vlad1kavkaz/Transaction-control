package org.txn.control.fincore.services.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.txn.control.fincore.model.Transaction;
import org.txn.control.fincore.model.TransactionsFilterRequestDto;
import org.txn.control.fincore.services.crud.TransactionService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaProcessingServiceTest {

    @InjectMocks
    private KafkaProcessingService kafkaProcessingService;

    @Mock
    private TransactionService transactionService;

    @Test
    void testGetTransactions() {
        // Arrange
        UUID userId = UUID.randomUUID();
        TransactionsFilterRequestDto requestDto = new TransactionsFilterRequestDto()
                .userId(userId)
                .type(TransactionsFilterRequestDto.TypeEnum.EXPENSE)
                .startDate(LocalDate.now().minusDays(7))
                .endDate(LocalDate.now())
                .page(1)
                .size(10);

        List<Transaction> mockTransactions = List.of(
                new Transaction().id(UUID.randomUUID()).userId(userId).type(Transaction.TypeEnum.EXPENSE)
        );
        when(transactionService.getTransactions(
                userId,
                requestDto.getType().toString(),
                requestDto.getStartDate(),
                requestDto.getEndDate(),
                requestDto.getPage(),
                requestDto.getSize()
        )).thenReturn(mockTransactions);

        // Act
        List<Transaction> result = kafkaProcessingService.getTransactions(requestDto);

        // Assert
        assertThat(result).isEqualTo(mockTransactions);
        verify(transactionService, times(1)).getTransactions(
                userId,
                requestDto.getType().toString(),
                requestDto.getStartDate(),
                requestDto.getEndDate(),
                requestDto.getPage(),
                requestDto.getSize()
        );
    }
}
