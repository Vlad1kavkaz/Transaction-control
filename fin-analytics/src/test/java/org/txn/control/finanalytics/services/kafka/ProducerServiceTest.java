package org.txn.control.finanalytics.services.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.txn.control.finanalytics.model.Category;
import org.txn.control.finanalytics.model.Transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProducerServiceTest {

    @InjectMocks
    private ProducerService kafkaProducerService;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void testSendResponse_Success() {
        // Arrange
        String topic = "banks-topic";
        String key = "12345";
        String payload = "Test Bank Response";

        // Act
        kafkaProducerService.sendRequest(topic, key, payload);

        // Assert
        ArgumentCaptor<Message<String>> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(kafkaTemplate, times(1)).send(messageCaptor.capture());

        Message<String> capturedMessage = messageCaptor.getValue();

        assertThat(capturedMessage).isNotNull();
        assertThat(capturedMessage.getPayload()).isEqualTo(payload);
        assertThat(capturedMessage.getHeaders().get(KafkaHeaders.TOPIC)).isEqualTo(topic);
        assertThat(capturedMessage.getHeaders().get(KafkaHeaders.KEY)).isEqualTo(key);
        assertThat(capturedMessage.getHeaders().get(KafkaHeaders.CORRELATION_ID)).isEqualTo(key);
        assertThat(capturedMessage.getHeaders().get("__TypeId__")).isEqualTo(payload.getClass().getName());
    }

    @Test
    void testSendResponse_WithNullPayload() {
        // Arrange
        String topic = "categories-topic";
        String key = "54321";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> kafkaProducerService.sendRequest(topic, key, null));
    }

    @Test
    void testSendResponse_EmptyKey() {
        // Arrange
        String topic = "transactions-topic";
        String key = "";
        Transaction transaction = new Transaction();

        // Act
        kafkaProducerService.sendRequest(topic, key, transaction);

        // Assert
        ArgumentCaptor<Message<Transaction>> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(kafkaTemplate, times(1)).send(messageCaptor.capture());

        Message<Transaction> capturedMessage = messageCaptor.getValue();

        assertThat(capturedMessage).isNotNull();
        assertThat(capturedMessage.getPayload()).isEqualTo(transaction);
        assertThat(capturedMessage.getHeaders().get(KafkaHeaders.TOPIC)).isEqualTo(topic);
        assertThat(capturedMessage.getHeaders().get(KafkaHeaders.KEY)).isEqualTo(key);
        assertThat(capturedMessage.getHeaders().get(KafkaHeaders.CORRELATION_ID)).isEqualTo(key);
        assertThat(capturedMessage.getHeaders().get("__TypeId__")).isEqualTo(transaction.getClass().getName());
    }

    @Test
    void testSendResponse_NullTopic() {
        // Arrange
        String topic = null;
        String key = "0000";
        Category category = new Category().name("Test Category");

        // Act
        kafkaProducerService.sendRequest(topic, key, category);

        // Assert
        ArgumentCaptor<Message<Category>> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(kafkaTemplate, times(1)).send(messageCaptor.capture());

        Message<Category> capturedMessage = messageCaptor.getValue();

        assertThat(capturedMessage).isNotNull();
        assertThat(capturedMessage.getPayload()).isEqualTo(category);
        assertThat(capturedMessage.getHeaders().get(KafkaHeaders.TOPIC)).isNull();
        assertThat(capturedMessage.getHeaders().get(KafkaHeaders.KEY)).isEqualTo(key);
        assertThat(capturedMessage.getHeaders().get(KafkaHeaders.CORRELATION_ID)).isEqualTo(key);
        assertThat(capturedMessage.getHeaders().get("__TypeId__")).isEqualTo(category.getClass().getName());
    }
}
