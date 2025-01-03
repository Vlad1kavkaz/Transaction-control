package org.txn.control.finanalytics.services.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.txn.control.finanalytics.model.Transaction;
import org.txn.control.finanalytics.services.AnalyticsService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsumerService {

    private final AnalyticsService analyticsService;

    @KafkaListener(
            topics = "${spring.kafka.topics.transactions-topic-out}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeTransactions(
            @Payload Object payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        log.info("Received transaction message with key: {}", key);
        List<Transaction> transactions = new ArrayList<>();

        if (payload instanceof List<?> list) {
            if (!list.isEmpty() && list.get(0) instanceof Transaction) {
                 transactions = castList(list, Transaction.class);
            }
        } else if (payload instanceof Transaction transaction) {
            transactions =  List.of(transaction);
        }

        analyticsService.completeRequest(key, transactions);
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> castList(List<?> list, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        for (Object item : list) {
            if (clazz.isInstance(item)) {
                result.add((T) item);
            } else {
                log.warn("Skipping item of unexpected type: {}", item.getClass().getName());
            }
        }
        return result;
    }
}
