package org.txn.control.histgen.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.txn.control.histgen.model.Bank;
import org.txn.control.histgen.services.BankService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BankControllerTest {

    @Mock
    private BankService bankService;

    @InjectMocks
    private BankController bankController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bankController).build();
    }

    @Test
    void getAllBanks_ReturnsListOfBanks() throws Exception {
        // Arrange
        List<Bank> banks = Arrays.asList(
                Bank.builder()
                        .name("Bank A")
                        .build(),
                Bank.builder()
                        .name("Bank B")
                        .build());

        when(bankService.getAllBanks()).thenReturn(banks);

        // Act & Assert
        mockMvc.perform(get("/v1/api/banks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Bank A"))
                .andExpect(jsonPath("$[1].name").value("Bank B"));
    }
}
