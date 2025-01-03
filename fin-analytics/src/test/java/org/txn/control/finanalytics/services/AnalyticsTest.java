package org.txn.control.finanalytics.services;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.txn.control.finanalytics.model.Transaction;
import org.txn.control.finanalytics.model.TransactionsFilterRequestDto;
import org.txn.control.finanalytics.services.kafka.ProducerService;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AnalyticsUtilsTest {

    @Mock
    private ProducerService producerService;

    @InjectMocks
    private AnalyticsUtils analyticsUtils;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(analyticsUtils, "TRANSACTION_TOPIC", "test-topic");
    }

    @Test
    void buildAndSendRequest_success() {
        UUID userId = UUID.randomUUID();
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        String type = "INCOME";

        Pair<String, CompletableFuture<List<Transaction>>> result =
                analyticsUtils.buildAndSendRequest(userId, startDate, endDate, type);

        assertThat(result).isNotNull();
        assertThat(result.getLeft()).isNotBlank();
        verify(producerService, times(1)).sendRequest(
                eq("test-topic"),
                eq(result.getLeft()),
                any(TransactionsFilterRequestDto.class)
        );
    }

    @Test
    void completeRequest_success() throws NoSuchFieldException, IllegalAccessException {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<List<Transaction>> future = new CompletableFuture<>();

        Field field = AnalyticsUtils.class.getDeclaredField("pendingRequests");
        field.setAccessible(true);

        @SuppressWarnings("unchecked")
        ConcurrentHashMap<String, CompletableFuture<List<Transaction>>> pendingRequests =
                (ConcurrentHashMap<String, CompletableFuture<List<Transaction>>>) field.get(analyticsUtils);

        pendingRequests.put(correlationId, future);

        List<Transaction> mockTransactions = List.of(new Transaction());
        analyticsUtils.completeRequest(correlationId, mockTransactions);

        assertThat(future).isCompletedWithValue(mockTransactions);
    }

    @Test
    void processAnalyticsRequest_success() {
        UUID userId = UUID.randomUUID();
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        String type = "EXPENSE";

        List<Transaction> mockTransactions = List.of(new Transaction());

        CompletableFuture<List<Transaction>> future = new CompletableFuture<>();
        future.complete(mockTransactions);

        doAnswer(invocation -> {
            String correlationId = invocation.getArgument(1, String.class);
            analyticsUtils.completeRequest(correlationId, mockTransactions);
            return null;
        }).when(producerService).sendRequest(anyString(), anyString(), any());

        Function<List<Transaction>, String> processor = transactions -> "Processed";
        String result = analyticsUtils.processAnalyticsRequest(userId, startDate, endDate, type, processor);

        assertThat(result).isEqualTo("Processed");
        verify(producerService, times(1)).sendRequest(anyString(), anyString(), any());
    }


    @Test
    void processAnalyticsRequest_executionException() {
        UUID userId = UUID.randomUUID();
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        String type = "EXPENSE";

        doAnswer(invocation -> {
            String correlationId = invocation.getArgument(1, String.class);
            analyticsUtils.completeRequest(correlationId, null);
            throw new ExecutionException("Test exception", null);
        }).when(producerService).sendRequest(anyString(), anyString(), any());

        assertThrows(ExecutionException.class,
                () -> analyticsUtils.processAnalyticsRequest(userId, startDate, endDate, type, transactions -> null));

        verify(producerService, times(1)).sendRequest(anyString(), anyString(), any());
    }
}

