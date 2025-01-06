package org.txn.control.fincore.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import feign.RetryableException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.txn.control.fincore.feign.clients.MsGenerateClient;
import org.txn.control.fincore.model.Bank;
import org.txn.control.fincore.model.Category;
import org.txn.control.fincore.model.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureWireMock(port = 11785)
@TestPropertySource(properties = "spring.liquibase.enabled=false")
class MsGenerateClientTest {

    @Autowired
    private MsGenerateClient msGenerateClient;

    private final String BASE_TRANSACTIONS_URL = "/transaction";
    private final String BASE_CATEGORIES_URL = "/categories";
    private final String BASE_BANKS_URL = "/banks";

    private final UUID userId = UUID.randomUUID();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @SneakyThrows
    void testTransaction_SuccessfulRequest_FirstAttempt() {
        List<Transaction> listResponse = new ArrayList<>();
        String response = objectMapper.writeValueAsString(listResponse);

        WireMock.stubFor(WireMock.get(BASE_TRANSACTIONS_URL + "/" + userId)
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));

        List<Transaction> actualListTransaction = msGenerateClient.transaction(userId);

        assertThat(actualListTransaction).isNotNull();
    }

    @Test
    @SneakyThrows
    void testTransaction_RetryOn500Failure_ThenSuccess() {
        List<Transaction> listResponse = new ArrayList<>();
        String response = objectMapper.writeValueAsString(listResponse);

        WireMock.stubFor(WireMock.get(BASE_TRANSACTIONS_URL + "/" + userId)
                .inScenario("Retry Transaction Scenario")
                .whenScenarioStateIs(STARTED)
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .willSetStateTo("Retry Successful"));

        WireMock.stubFor(WireMock.get(BASE_TRANSACTIONS_URL + "/" + userId)
                .inScenario("Retry Transaction Scenario")
                .whenScenarioStateIs("Retry Successful")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));

        List<Transaction> actualListTransaction = msGenerateClient.transaction(userId);

        assertThat(actualListTransaction).isNotNull();
        WireMock.verify(2, WireMock.getRequestedFor(WireMock.urlEqualTo(BASE_TRANSACTIONS_URL + "/" + userId)));
    }

    @Test
    void testTransaction_ExceedMaxRetries() {
        WireMock.stubFor(WireMock.get(BASE_TRANSACTIONS_URL + "/" + userId)
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));

        assertThrows(RetryableException.class, () -> msGenerateClient.transaction(userId));

        WireMock.verify(3, WireMock.getRequestedFor(WireMock.urlEqualTo(BASE_TRANSACTIONS_URL + "/" + userId)));
    }

    @Test
    void testTransaction_ExceptOn400() {
        WireMock.stubFor(WireMock.get(BASE_TRANSACTIONS_URL + "/" + userId)
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.BAD_REQUEST.value())));

        assertThrows(RetryableException.class, () -> msGenerateClient.transaction(userId));

        WireMock.verify(1, WireMock.getRequestedFor(WireMock.urlEqualTo(BASE_TRANSACTIONS_URL + "/" + userId)));
    }

    @Test
    @SneakyThrows
    void testBanks_SuccessfulRequest_FirstAttempt() {
        List<Bank> listResponse = new ArrayList<>();
        String response = objectMapper.writeValueAsString(listResponse);

        WireMock.stubFor(WireMock.get(BASE_BANKS_URL)
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));

        List<Bank> banks = msGenerateClient.banks();

        assertThat(banks).isNotNull();
    }

    @Test
    void testBanks_RetryOn500Failure_ThenSuccess() {
        WireMock.stubFor(WireMock.get(BASE_BANKS_URL)
                .inScenario("Retry Bank Scenario")
                .whenScenarioStateIs(STARTED)
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .willSetStateTo("Retry Successful"));

        WireMock.stubFor(WireMock.get(BASE_BANKS_URL)
                .inScenario("Retry Bank Scenario")
                .whenScenarioStateIs("Retry Successful")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));

        List<Bank> banks = msGenerateClient.banks();

        assertThat(banks).isNotNull();
    }

    @Test
    void testBanks_ExceedMaxRetries() {
        WireMock.stubFor(WireMock.get(BASE_BANKS_URL)
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));

        assertThrows(RetryableException.class, () -> msGenerateClient.banks());
    }

    @Test
    void testBanks_ExceptOn400() {
        WireMock.stubFor(WireMock.get(BASE_BANKS_URL)
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.BAD_REQUEST.value())));

        assertThrows(RetryableException.class, () -> msGenerateClient.banks());
    }

    @Test
    @SneakyThrows
    void testCategories_SuccessfulRequest_FirstAttempt() {
        WireMock.stubFor(WireMock.get(BASE_CATEGORIES_URL)
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));

        List<Category> categories = msGenerateClient.categories();

        assertThat(categories).isNotNull();
    }

    @Test
    void testCategories_ExceedMaxRetries() {
        WireMock.stubFor(WireMock.get(BASE_CATEGORIES_URL)
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));

        assertThrows(RetryableException.class, () -> msGenerateClient.categories());
    }

    @Test
    @SneakyThrows
    void testCategories_RetryOn500Failure_ThenSuccess() {
        // Arrange
        WireMock.resetAllRequests();

        List<Category> listResponse = new ArrayList<>();
        String response = objectMapper.writeValueAsString(listResponse);

        WireMock.stubFor(WireMock.get(BASE_CATEGORIES_URL)
                .inScenario("Retry scenario - Categories")
                .whenScenarioStateIs(STARTED)
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .willSetStateTo("Retry Successful"));
        WireMock.stubFor(WireMock.get(BASE_CATEGORIES_URL)
                .inScenario("Retry scenario - Categories")
                .whenScenarioStateIs("Retry Successful")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));

        // Act
        List<Category> actualListCategory = msGenerateClient.categories();

        // Assert
        assertThat(actualListCategory).isNotNull();
    }

    @Test
    void testCategories_ExceptOn400() {
        // Arrange
        WireMock.resetAllRequests();

        WireMock.stubFor(WireMock.get(BASE_CATEGORIES_URL)
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.BAD_REQUEST.value())));

        // Act
        assertThrows(RetryableException.class, () ->
                msGenerateClient.categories());

        // Assert
        WireMock.verify(1, WireMock.getRequestedFor(WireMock.urlEqualTo(BASE_CATEGORIES_URL)));
    }

}
