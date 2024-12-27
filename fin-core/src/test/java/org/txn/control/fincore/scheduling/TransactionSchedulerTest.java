package org.txn.control.fincore.scheduling;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.txn.control.fincore.feign.clients.MsGenerateClient;
import org.txn.control.fincore.model.Bank;
import org.txn.control.fincore.model.Category;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionSchedulerTest {

    @InjectMocks
    private TransactionScheduler scheduler;

    @Mock
    private MsGenerateClient client;

    @Mock
    private ProcessingServiceDao serviceDao;

    private List<Bank> banks;
    private List<Category> categories;

    @BeforeEach
    void setUp() {
        Bank bank1 = new Bank();
        bank1.setId(UUID.randomUUID());
        bank1.setName("Bank1");
        bank1.setCountry("Russia");

        Bank bank2 = new Bank();
        bank2.setId(UUID.randomUUID());
        bank2.setName("Bank2");
        bank2.setCountry("USA");

        banks = List.of(bank1, bank2);

        Category category1 = new Category();
        category1.setId(UUID.randomUUID());
        category1.setName("Groceries");

        Category category2 = new Category();
        category2.setId(UUID.randomUUID());
        category2.setName("Electronics");

        categories = List.of(category1, category2);
    }

    @Test
    void testScheduleBanks_Success() {
        // Arrange
        when(client.banks()).thenReturn(banks);

        // Act
        scheduler.scheduleBanks();

        // Assert
        verify(client, times(1)).banks();
        verify(serviceDao, times(1)).processBanks(banks);
    }

    @Test
    void testScheduleBanks_EmptyList() {
        // Arrange
        when(client.banks()).thenReturn(List.of());

        // Act
        scheduler.scheduleBanks();

        // Assert
        verify(client, times(1)).banks();
        verify(serviceDao, times(1)).processBanks(List.of());
    }

    @Test
    void testScheduleBanks_ExceptionThrown() {
        // Arrange
        when(client.banks()).thenThrow(new RuntimeException("API error"));

        // Act & Assert
        assertThatThrownBy(() -> scheduler.scheduleBanks())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("API error");

        verify(client, times(1)).banks();
        verifyNoInteractions(serviceDao);
    }

    @Test
    void testScheduleCategories_Success() {
        // Arrange
        when(client.categories()).thenReturn(categories);

        // Act
        scheduler.scheduleCategories();

        // Assert
        verify(client, times(1)).categories();
        verify(serviceDao, times(1)).processCategories(categories);
    }

    @Test
    void testScheduleCategories_EmptyList() {
        // Arrange
        when(client.categories()).thenReturn(List.of());

        // Act
        scheduler.scheduleCategories();

        // Assert
        verify(client, times(1)).categories();
        verify(serviceDao, times(1)).processCategories(List.of());
    }

    @Test
    void testScheduleCategories_ExceptionThrown() {
        // Arrange
        when(client.categories()).thenThrow(new RuntimeException("API error"));

        // Act & Assert
        assertThatThrownBy(() -> scheduler.scheduleCategories())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("API error");

        verify(client, times(1)).categories();
        verifyNoInteractions(serviceDao);
    }
}
