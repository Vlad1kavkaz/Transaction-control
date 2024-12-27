package org.txn.control.fincore.services.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.txn.control.fincore.model.Bank;
import org.txn.control.fincore.model.Category;
import org.txn.control.fincore.model.Transaction;
import org.txn.control.fincore.model.TransactionRequestDto;
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
    void testConsumeBanks_WithStringPayload() {
        // Arrange
        String country = "Russia";
        Bank bank = new Bank();
        bank.setId(UUID.randomUUID());
        bank.setName("Bank1");
        bank.setCountry(country);
        List<Bank> banks = List.of(bank);
        when(processingService.getBanks(country)).thenReturn(banks);

        // Act
        kafkaConsumerService.consumeBanks(country, key);

        // Assert
        verify(processingService).getBanks(country);
        verify(kafkaProducerService).sendResponse(any(), eq(key), eq(banks));
    }

    @Test
    void testConsumeBanks_WithNonStringPayload() {
        // Arrange
        Object invalidPayload = new Object();

        // Act
        kafkaConsumerService.consumeBanks(invalidPayload, key);

        // Assert
        verifyNoInteractions(processingService);
        verifyNoInteractions(kafkaProducerService);
    }

    @Test
    void testConsumeCategories() {
        // Arrange
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName("Electronics");
        List<Category> categories = List.of(category);
        when(processingService.getCategories()).thenReturn(categories);

        // Act
        kafkaConsumerService.consumeCategories("SomePayload", key);

        // Assert
        verify(processingService).getCategories();
        verify(kafkaProducerService).sendResponse(any(), eq(key), eq(categories));
    }

    @Test
    void testConsumeTransaction_ValidPayload() {
        // Arrange
        TransactionRequestDto requestDto =
                new TransactionRequestDto(UUID.randomUUID(), TransactionRequestDto.TypeEnum.EXPENSE);
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setUserId(UUID.randomUUID());
        when(processingService.getTransaction(requestDto.getTransactionId(), requestDto.getType().toString()))
                .thenReturn(transaction);

        // Act
        kafkaConsumerService.consumeTransaction(requestDto, key);

        // Assert
        verify(processingService).getTransaction(requestDto.getTransactionId(), requestDto.getType().toString());
        verify(kafkaProducerService).sendResponse(any(), eq(key), eq(transaction));
    }

    @Test
    void testConsumeTransaction_InvalidPayload() {
        // Arrange
        Object invalidPayload = new Object();

        // Act
        kafkaConsumerService.consumeTransaction(invalidPayload, key);

        // Assert
        verifyNoInteractions(processingService);
        verifyNoInteractions(kafkaProducerService);
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
