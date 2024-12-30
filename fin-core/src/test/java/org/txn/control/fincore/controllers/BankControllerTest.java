package org.txn.control.fincore.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.txn.control.fincore.model.Bank;
import org.txn.control.fincore.services.crud.BankService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankControllerTest {

    @Mock
    private BankService bankService;

    @InjectMocks
    private BankController bankController;

    private List<Bank> bankList;

    @BeforeEach
    void setUp() {
        Bank bank1 = new Bank().id(UUID.randomUUID()).name("Sberbank").country("Russia");
        Bank bank2 = new Bank().id(UUID.randomUUID()).name("HSBC").country("UK");
        bankList = List.of(bank1, bank2);
    }

    @Test
    void testBanksGet_ReturnsBankList() {
        // Arrange
        String country = "Russia";
        when(bankService.findBanks(country)).thenReturn(bankList);

        // Act
        ResponseEntity<List<Bank>> response = bankController.banksGet(country);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertEquals(bankList, response.getBody());
    }

    @Test
    void testBanksGet_EmptyList() {
        // Arrange
        String country = "France";
        when(bankService.findBanks(country)).thenReturn(List.of());

        // Act
        ResponseEntity<List<Bank>> response = bankController.banksGet(country);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertEquals(0, Objects.requireNonNull(response.getBody()).size());
    }
}
