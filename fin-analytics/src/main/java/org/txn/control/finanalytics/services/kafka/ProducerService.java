package org.txn.control.finanalytics.services.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public <T> void sendRequest(String topic, String key, T response) {
        Message<T> message = MessageBuilder
                .withPayload(response)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, key)
                .setHeader(KafkaHeaders.CORRELATION_ID, key)
                .setHeader("__TypeId__", response.getClass().getName())
                .build();

        kafkaTemplate.send(message);
        log.info("Sent response 'get banks' to topic '{}' with key '{}'", topic, key);
    }
}
