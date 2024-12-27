package org.txn.control.fincore.feign;

import feign.RetryableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FinCoreRetryerTest {

    private FinCoreRetryer retryer;

    private final int maxAttempts = 3;

    @BeforeEach
    void setUp() {
        long period = 100;
        long maxPeriod = 1000;
        retryer = new FinCoreRetryer(period, maxPeriod, maxAttempts);
    }

    @Test
    void testContinueOrPropagate_WithStatusCode500_ShouldRetry() {
        // Arrange
        RetryableException exception = mock(RetryableException.class);
        when(exception.status()).thenReturn(500);

        // Act & Assert
        assertDoesNotThrow(() -> {
            retryer.continueOrPropagate(exception);
            assertEquals(2, retryer.getAttempt());
        });
    }

    @Test
    void testContinueOrPropagate_WithStatusCode400_ShouldThrowException() {
        // Arrange
        RetryableException exception = mock(RetryableException.class);
        when(exception.status()).thenReturn(400);

        // Act & Assert
        assertThrows(RetryableException.class, () -> retryer.continueOrPropagate(exception));
        assertEquals(1, retryer.getAttempt());
    }

    @Test
    void testContinueOrPropagate_WithMaxAttemptsExceeded_ShouldThrowException() {
        // Arrange
        RetryableException exception = mock(RetryableException.class);
        when(exception.status()).thenReturn(500);

        // Act
        for (int i = 1; i < maxAttempts; i++) {
            assertDoesNotThrow(() -> retryer.continueOrPropagate(exception));
        }

        // Assert
        assertThrows(RetryableException.class, () -> retryer.continueOrPropagate(exception));
        assertEquals(maxAttempts, retryer.getAttempt());
    }

    @Test
    void testContinueOrPropagate_WithStatus404_ShouldDoNothing() {
        // Arrange
        RetryableException exception = mock(RetryableException.class);
        when(exception.status()).thenReturn(404);
        FinCoreRetryer clonedRetryer = (FinCoreRetryer) retryer.clone();

        // Act
        assertDoesNotThrow(() -> retryer.continueOrPropagate(exception));

        // Assert
        assertNotSame(retryer, clonedRetryer);
        assertEquals(retryer.getPeriod(), clonedRetryer.getPeriod());
        assertEquals(retryer.getMaxPeriod(), clonedRetryer.getMaxPeriod());
        assertEquals(retryer.getMaxAttempts(), clonedRetryer.getMaxAttempts());
    }

    @Test
    void testContinueOrPropagate_InterruptedException_ShouldThrowRetryableException() {
        // Arrange
        RetryableException exception = mock(RetryableException.class);
        when(exception.status()).thenReturn(500);
        Thread.currentThread().interrupt();

        // Act & Assert
        assertThrows(RetryableException.class, () -> retryer.continueOrPropagate(exception));
    }

    @Test
    void testClone_ShouldCreateNewInstanceWithSameParameters() {
        // Arrange
        FinCoreRetryer clonedRetryer = (FinCoreRetryer) retryer.clone();

        //Act & Assert
        assertNotSame(retryer, clonedRetryer);
        assertEquals(retryer.getPeriod(), clonedRetryer.getPeriod());
        assertEquals(retryer.getMaxPeriod(), clonedRetryer.getMaxPeriod());
        assertEquals(retryer.getMaxAttempts(), clonedRetryer.getMaxAttempts());
    }
}
