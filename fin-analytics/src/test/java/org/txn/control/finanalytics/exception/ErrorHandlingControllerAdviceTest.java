package org.txn.control.finanalytics.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ErrorHandlingControllerAdviceTest {

    @InjectMocks
    private ErrorHandlingControllerAdvice controllerAdvice;

    @Test
    void test_OnRuntimeException() {
        // Arrange
        String expectedErrorMessage = "Runtime exception occurred";
        RuntimeException exception = Mockito.mock(RuntimeException.class);
        when(exception.getMessage()).thenReturn(expectedErrorMessage);

        // Act
        String actualErrorMessage = controllerAdvice.onRuntimeException(exception);

        // Assert
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    void test_OnException() {
        // Arrange
        String expectedErrorMessage = "General exception occurred";
        Exception exception = Mockito.mock(Exception.class);
        when(exception.getMessage()).thenReturn(expectedErrorMessage);

        // Act
        String actualErrorMessage = controllerAdvice.onException(exception);

        // Assert
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }
}
