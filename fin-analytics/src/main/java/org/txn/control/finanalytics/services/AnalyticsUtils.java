package org.txn.control.finanalytics.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.txn.control.finanalytics.model.Transaction;
import org.txn.control.finanalytics.model.TransactionsFilterRequestDto;
import org.txn.control.finanalytics.services.kafka.ProducerService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsUtils {

    @Value("${spring.kafka.topics.transactions-topic-in}")
    private String TRANSACTION_TOPIC;

    private final ProducerService producerService;

    private final ConcurrentHashMap<String, CompletableFuture<List<Transaction>>> pendingRequests =
            new ConcurrentHashMap<>();

    public void completeRequest(String correlationId, List<Transaction> transactions) {
        CompletableFuture<List<Transaction>> future = pendingRequests.remove(correlationId);
        if (future != null) {
            future.complete(transactions);
        } else {
            log.warn("No pending request transaction found for correlationId: {}", correlationId);
        }
    }

    public Pair<String, CompletableFuture<List<Transaction>>> buildAndSendRequest(
            UUID userId,
            LocalDate startDate,
            LocalDate endDate,
            String type) {

        String correlationId = UUID.randomUUID().toString();

        TransactionsFilterRequestDto request = new TransactionsFilterRequestDto();
        request.setUserId(userId);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setType(type == null ? null : TransactionsFilterRequestDto.TypeEnum.valueOf(type));

        CompletableFuture<List<Transaction>> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);
        producerService.sendRequest(TRANSACTION_TOPIC, correlationId, request);

        return Pair.of(correlationId, future);
    }

    public <T> T processAnalyticsRequest(
            UUID userId,
            LocalDate startDate,
            LocalDate endDate,
            String type, Function<List<Transaction>, T> processor) {

        Pair<String, CompletableFuture<List<Transaction>>> request =
                buildAndSendRequest(userId, startDate, endDate, type);

        String correlationId = request.getLeft();
        CompletableFuture<List<Transaction>> future = request.getRight();

        try {
            List<Transaction> transactions = future.get();
            return processor.apply(transactions);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to get response from Kafka", e);
        } finally {
            pendingRequests.remove(correlationId);
        }
    }
}
