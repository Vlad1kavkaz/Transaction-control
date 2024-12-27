package org.txn.control.fincore.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(value = "spring.kafka.enabled", havingValue = "true")
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.key-serializer}")
    private String keySerializer;

    @Value("${spring.kafka.producer.value-serializer}")
    private String valueSerializer;

    @Value("${spring.kafka.producer.retries}")
    private int retries;

    @Value("${spring.kafka.producer.linger-ms}")
    private int lingerMs;

    @Value("${spring.kafka.producer.batch-size}")
    private int batchSize;

    @Value("${spring.kafka.producer.compression-type}")
    private String compressionType;

    @Value("${spring.kafka.producer.max-in-flight-requests}")
    private int maxInFlightRequests;

    @Value("${spring.kafka.producer.request-timeout-ms}")
    private int requestTimeoutMs;

    @Value("${spring.kafka.producer.delivery-timeout-ms}")
    private int deliveryTimeoutMs;

    @Bean
    public Map<String, Object> producerProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);

        properties.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionType);

        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMs);
        properties.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeoutMs);
        properties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, maxInFlightRequests);
        properties.put(ProducerConfig.RETRIES_CONFIG, retries);

        return properties;
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerProperties());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        KafkaTemplate<String, Object> kafkaTemplate = new KafkaTemplate<>(producerFactory());
        kafkaTemplate.setObservationEnabled(true);
        return kafkaTemplate;
    }
}
