package org.txn.control.fincore.exception;

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
    void test_OnExpenseNotFoundException() {
        // Arrange
        String expectedErrorMessage = "Expense not found with id: 1";
        ExpenseNotFoundException exception = Mockito.mock(ExpenseNotFoundException.class);
        when(exception.getMessage()).thenReturn(expectedErrorMessage);

        // Act
        String actualErrorMessage = controllerAdvice.onExpenseNotFoundException(exception);

        // Assert
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    void test_OnIncomeNotFoundException() {
        // Arrange
        String expectedErrorMessage = "Income not found with id: 2";
        IncomeNotFoundException exception = Mockito.mock(IncomeNotFoundException.class);
        when(exception.getMessage()).thenReturn(expectedErrorMessage);

        // Act
        String actualErrorMessage = controllerAdvice.onIncomeNotFoundException(exception);

        // Assert
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    void test_OnUserNotFoundException() {
        // Arrange
        String expectedErrorMessage = "User not found with id: 3";
        UserNotFoundException exception = Mockito.mock(UserNotFoundException.class);
        when(exception.getMessage()).thenReturn(expectedErrorMessage);

        // Act
        String actualErrorMessage = controllerAdvice.onUserNotFoundException(exception);

        // Assert
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    void test_OnCategoryNotFoundException() {
        // Arrange
        String expectedErrorMessage = "Category not found with id: 4";
        CategoryNotFoundException exception = Mockito.mock(CategoryNotFoundException.class);
        when(exception.getMessage()).thenReturn(expectedErrorMessage);

        // Act
        String actualErrorMessage = controllerAdvice.onCategoryNotFoundException(exception);

        // Assert
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

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
