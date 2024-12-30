package org.txn.control.histgen.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.txn.control.histgen.model.Bank;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankServiceTest {

    @InjectMocks
    private BankService bankService;

    @Mock
    private List<Bank> sendBanks;

    @Test
    void getAllBanks_ShouldReturnBanksAndClearQueue() {
        // Arrange
        List<Bank> initialBanks = new ArrayList<>(bankService.getAllBanks());
        when(sendBanks.addAll(initialBanks)).thenReturn(true);

        // Act
        List<Bank> result = bankService.getAllBanks();

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void scheduled_ShouldAddBanksToQueue() throws Exception {
        // Act
        Method scheduledMethod = BankService.class.getDeclaredMethod("scheduled");
        scheduledMethod.setAccessible(true);
        scheduledMethod.invoke(bankService);

        List<Bank> result = bankService.getAllBanks();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Сбербанк");
    }
}
