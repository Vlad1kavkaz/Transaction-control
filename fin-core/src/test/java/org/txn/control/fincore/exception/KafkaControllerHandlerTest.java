package org.txn.control.fincore.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaControllerHandlerTest {

    @InjectMocks
    private KafkaControllerHandler kafkaControllerHandler;

    @Mock
    private Message<String> message;

    @Mock
    private ListenerExecutionFailedException exception;

    @Mock
    private MethodArgumentNotValidException validationException;

    @Mock
    private BindingResult bindingResult;

    @Test
    void testHandleError_WithValidationException() {
        // Arrange
        FieldError fieldError = new FieldError("object", "field", "must not be null");
        when(validationException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(exception.getCause()).thenReturn(validationException);
        when(message.getPayload()).thenReturn("Test Payload");

        // Act
        kafkaControllerHandler.handleError(message, exception);

        // Assert
        verify(bindingResult, times(1)).hasErrors();
        verify(bindingResult, times(1)).getFieldErrors();
        assertEquals("field", fieldError.getField());
        assertEquals("must not be null", fieldError.getDefaultMessage());
    }

    @Test
    void testHandleError_WithoutValidationException() {
        // Arrange
        when(exception.getCause()).thenReturn(new RuntimeException("Some other error"));

        // Act
        Object result = kafkaControllerHandler.handleError(message, exception);

        // Assert
        assertNotNull(result);
        verifyNoInteractions(bindingResult);
    }

    @Test
    void testHandleError_WithNullBindingResult() {
        // Arrange
        when(validationException.getBindingResult()).thenReturn(null);
        when(exception.getCause()).thenReturn(validationException);

        // Act
        Object result = kafkaControllerHandler.handleError(message, exception);

        // Assert
        assertNotNull(result);
        verify(validationException, times(1)).getBindingResult();
    }

    @Test
    void testHandleError_WithNoErrors() {
        // Arrange
        when(validationException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(exception.getCause()).thenReturn(validationException);

        // Act
        Object result = kafkaControllerHandler.handleError(message, exception);

        // Assert
        assertNotNull(result);
        verify(bindingResult, times(1)).hasErrors();
        verify(bindingResult, never()).getFieldErrors();
    }

    @Test
    void testHandleError_NullMessageThrowsNPE() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> kafkaControllerHandler.handleError(null, exception));
    }
}
