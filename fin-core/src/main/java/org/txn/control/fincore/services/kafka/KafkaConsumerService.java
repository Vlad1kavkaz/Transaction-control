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
import org.txn.control.fincore.model.Transaction;
import org.txn.control.fincore.model.TransactionsFilterRequestDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    @Value("${spring.kafka.topics.transactions-topic-out}")
    private String transactionTopic;

    private final KafkaProcessingService processingService;
    private final KafkaProducerService kafkaProducerService;

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
