package org.txn.control.fincore.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListenerConfigurer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.mapping.Jackson2JavaTypeMapper;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(value = "spring.kafka.enabled", havingValue = "true")
public class KafkaConsumerConfig implements KafkaListenerConfigurer {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String consumerGroup;

    @Value("${spring.kafka.consumer.key-deserializer}")
    private String keyDeserializer;

    @Value("${spring.kafka.consumer.backoff-interval-millis}")
    private long backoffIntervalMillis;

    @Value("${spring.kafka.consumer.backoff-max-failure-count}")
    private int backoffMaxFailureCount;

    private final LocalValidatorFactoryBean validator;

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);

        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        properties.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());

        properties.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(properties);
    }


    @Bean
    DefaultErrorHandler defaultErrorHandler() {
        BackOff fixedBackOff = new FixedBackOff(backoffIntervalMillis, backoffMaxFailureCount);
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(fixedBackOff);
        errorHandler.addNotRetryableExceptions(SocketTimeoutException.class);
        return errorHandler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(defaultErrorHandler());
        return factory;
    }

    @Bean
    public Jackson2JavaTypeMapper typeMapper() {
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();

        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("org.txn.control.fincore.model.Bank", org.txn.control.fincore.model.Bank.class);
        idClassMapping.put("org.txn.control.fincore.model.Category", org.txn.control.fincore.model.Category.class);
        idClassMapping.put("org.txn.control.fincore.model.TransactionRequestDto", org.txn.control.fincore.model.TransactionRequestDto.class);
        idClassMapping.put("org.txn.control.fincore.model.TransactionsFilterRequestDto", org.txn.control.fincore.model.TransactionsFilterRequestDto.class);

        typeMapper.setIdClassMapping(idClassMapping);
        return typeMapper;
    }

    @Override
    public void configureKafkaListeners(KafkaListenerEndpointRegistrar registrar) {
        registrar.setValidator(validator);
    }
}
