package org.txn.control.finanalytics.exception;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Slf4j
@Component
@ConditionalOnProperty(value = "spring.kafka.enabled", havingValue = "true")
public class KafkaErrorControllerHandler implements KafkaListenerErrorHandler {

    @Override
    public @NonNull Object handleError(@NonNull Message<?> message, ListenerExecutionFailedException exception) {
        if (exception.getCause() instanceof MethodArgumentNotValidException validationException) {
            BindingResult bindingResult = validationException.getBindingResult();
            if (bindingResult != null && bindingResult.hasErrors()) {
                StringBuilder errorMessages = new StringBuilder();

                for (FieldError fieldError : bindingResult.getFieldErrors()) {
                    errorMessages
                            .append(fieldError.getField())
                            .append(" - ")
                            .append(fieldError.getDefaultMessage())
                            .append("\n");
                }

                log.error("Message - {}, Error - {}", message.getPayload(), errorMessages);
            }
        }
        return new Object();
    }
}
