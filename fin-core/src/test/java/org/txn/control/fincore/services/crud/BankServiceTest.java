package org.txn.control.fincore.services.crud;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.txn.control.fincore.entities.BankEntity;
import org.txn.control.fincore.mappers.ResponseMapper;
import org.txn.control.fincore.model.Bank;
import org.txn.control.fincore.repositories.BankRepository;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankServiceTest {

    @InjectMocks
    private BankService bankService;

    @Mock
    private BankRepository bankRepository;

    @Mock
    private ResponseMapper mapper;

    private List<BankEntity> bankEntities;
    private List<Bank> banks;

    @BeforeEach
    void setUp() {
        BankEntity bankEntity1 = new BankEntity(UUID.randomUUID(), "Bank1", "Russia");
        BankEntity bankEntity2 = new BankEntity(UUID.randomUUID(), "Bank2", "USA");
        bankEntities = List.of(bankEntity1, bankEntity2);

        Bank bank1 = new Bank();
        bank1.setId(bankEntity1.getId());
        bank1.setName(bankEntity1.getName());
        bank1.setCountry(bankEntity1.getCountry());

        Bank bank2 = new Bank();
        bank2.setId(bankEntity2.getId());
        bank2.setName(bankEntity2.getName());
        bank2.setCountry(bankEntity2.getCountry());

        banks = List.of(bank1, bank2);
    }

    @Test
    void testFindBanks_WithCountry() {
        // Arrange
        when(bankRepository.findByCountry("Russia")).thenReturn(bankEntities);
        when(mapper.toResponseBank(any(BankEntity.class))).thenReturn(banks.get(0));

        // Act
        List<Bank> result = bankService.findBanks("Russia");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        verify(bankRepository, times(1)).findByCountry("Russia");
        verify(mapper, times(2)).toResponseBank(any(BankEntity.class));
    }

    @Test
    void testFindBanks_WithoutCountry() {
        // Arrange
        when(bankRepository.findAll()).thenReturn(bankEntities);
        when(mapper.toResponseBank(any(BankEntity.class))).thenReturn(banks.get(0), banks.get(1));

        // Act
        List<Bank> result = bankService.findBanks(null);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        verify(bankRepository, times(1)).findAll();
        verify(mapper, times(2)).toResponseBank(any(BankEntity.class));
    }

    @Test
    void testFindBanks_EmptyResult() {
        // Arrange
        when(bankRepository.findByCountry("France")).thenReturn(List.of());

        // Act
        List<Bank> result = bankService.findBanks("France");

        // Assert
        assertThat(result).isEmpty();
        verify(bankRepository, times(1)).findByCountry("France");
        verifyNoInteractions(mapper);
    }
}
