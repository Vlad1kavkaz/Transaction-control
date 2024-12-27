package org.txn.control.fincore.services.kafka;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.txn.control.fincore.model.Bank;
import org.txn.control.fincore.model.Category;
import org.txn.control.fincore.model.Transaction;
import org.txn.control.fincore.model.TransactionRequestDto;
import org.txn.control.fincore.model.TransactionsFilterRequestDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    @Value("${spring.kafka.topics.banks-topic-out}")
    private String bankTopic;

    @Value("${spring.kafka.topics.categories-topic-out}")
    private String categoryTopic;

    @Value("${spring.kafka.topics.transactions-topic-out}")
    private String transactionTopic;

    private final KafkaProcessingService processingService;
    private final KafkaProducerService kafkaProducerService;

    @KafkaListener(
            topics = "${spring.kafka.topics.banks-topic-in}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeBanks(
            @Payload Object payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        log.info("Received bank message with key: {}, payload class: {}", key, payload.getClass().getSimpleName());

        if (payload instanceof String country) {
            List<Bank> banks = processingService.getBanks(country);
            kafkaProducerService.sendResponse(bankTopic, key, banks);
        }
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.categories-topic-in}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeCategories(
            @Payload Object payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        log.info("Received category message with key: {}", key);

        List<Category> categories = processingService.getCategories();
        kafkaProducerService.sendResponse(categoryTopic, key, categories);
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.transactions-topic-in}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeTransaction(
            @Payload @Valid Object payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        if (payload instanceof TransactionRequestDto transactionRequest) {
            log.info("Received transaction request for id: {}, key: {}",
                    transactionRequest.getTransactionId(), key);

            Transaction transaction = processingService.getTransaction(
                    transactionRequest.getTransactionId(), transactionRequest.getType().toString());
            kafkaProducerService.sendResponse(transactionTopic, key, transaction);
        }
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.transactions-topic-in}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeFilteredTransaction(
            @Payload @Valid Object payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        if (payload instanceof TransactionsFilterRequestDto filterRequest) {
            log.info("Received filtered transaction for user id: {}, key: {}",
                    filterRequest.getType(), key);

            List<Transaction> transactions = processingService.getTransactions(filterRequest);
            kafkaProducerService.sendResponse(transactionTopic, key, transactions);
        }
    }
}
