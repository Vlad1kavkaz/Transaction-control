package org.txn.control.finanalytics.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.txn.control.finanalytics.model.BankIncomeResponseInner;
import org.txn.control.finanalytics.model.BankSummaryResponseInner;
import org.txn.control.finanalytics.model.CategoryExpenseResponseInner;
import org.txn.control.finanalytics.model.IncomeVsExpenseResponse;
import org.txn.control.finanalytics.model.MonthlyHistoryResponseInner;
import org.txn.control.finanalytics.model.PredictionResponseInner;
import org.txn.control.finanalytics.model.SummaryResponse;
import org.txn.control.finanalytics.model.TopExpensesResponseInner;
import org.txn.control.finanalytics.services.AnalyticsService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsControllerTest {

    @Mock
    private AnalyticsService analyticsService;

    @InjectMocks
    private AnalyticsController analyticsController;

    private UUID userId;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        startDate = LocalDate.of(2023, 1, 1);
        endDate = LocalDate.of(2023, 12, 31);
    }

    @Test
    void testAnalyticsBankSummaryUserIdGet() {
        List<BankSummaryResponseInner> mockResponse = List.of(
                BankSummaryResponseInner.builder().bankName("Bank A").income(BigDecimal.valueOf(1000)).build(),
                BankSummaryResponseInner.builder().bankName("Bank B").income(BigDecimal.valueOf(2000)).build()
        );

        when(analyticsService.analyticsBankSummaryUserIdGet(userId)).thenReturn(mockResponse);

        ResponseEntity<List<BankSummaryResponseInner>> response = analyticsController
                .analyticsBankSummaryUserIdGet(userId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void testAnalyticsIncomeVsExpenseUserIdGet() {
        IncomeVsExpenseResponse mockResponse = IncomeVsExpenseResponse.builder()
                    .income(BigDecimal.valueOf(10000))
                    .expense(BigDecimal.valueOf(7000))
                        .build();

        when(analyticsService.analyticsIncomeVsExpenseUserIdGet(userId)).thenReturn(mockResponse);

        ResponseEntity<IncomeVsExpenseResponse> response = analyticsController
                .analyticsIncomeVsExpenseUserIdGet(userId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void testAnalyticsIncomesByBankUserIdGet() {
        List<BankIncomeResponseInner> mockResponse = List.of(
                BankIncomeResponseInner.builder().bankName("Bank A").totalIncome(BigDecimal.valueOf(1000)).build(),
                BankIncomeResponseInner.builder().bankName("Bank B").totalIncome(BigDecimal.valueOf(2000)).build()
        );

        when(analyticsService.analyticsIncomesByBankUserIdGet(userId)).thenReturn(mockResponse);

        ResponseEntity<List<BankIncomeResponseInner>> response = analyticsController
                .analyticsIncomesByBankUserIdGet(userId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void testAnalyticsMonthlyHistoryUserIdGet() {
        List<MonthlyHistoryResponseInner> mockResponse = List.of(
                MonthlyHistoryResponseInner.builder().month("January").income(BigDecimal.valueOf(1000)).build(),
                MonthlyHistoryResponseInner.builder().month("February").income(BigDecimal.valueOf(2000)).build()
        );

        when(analyticsService.analyticsMonthlyHistoryUserIdGet(userId)).thenReturn(mockResponse);

        ResponseEntity<List<MonthlyHistoryResponseInner>> response = analyticsController
                .analyticsMonthlyHistoryUserIdGet(userId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void testAnalyticsPredictionUserIdGet() {
        List<PredictionResponseInner> mockResponse = List.of(
                PredictionResponseInner.builder().category("Food").predictedAmount(BigDecimal.valueOf(1500)).build(),
                PredictionResponseInner.builder().category("Presents").predictedAmount(BigDecimal.valueOf(1800)).build()
        );

        when(analyticsService.analyticsPredictionUserIdGet(userId)).thenReturn(mockResponse);

        ResponseEntity<List<PredictionResponseInner>> response = analyticsController
                .analyticsPredictionUserIdGet(userId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void testAnalyticsSummaryUserIdGet() {
        SummaryResponse mockResponse = SummaryResponse.builder()
                .userId(userId)
                .totalIncome(BigDecimal.valueOf(10000))
                .totalExpense(BigDecimal.valueOf(7000))
                .balance(BigDecimal.valueOf(3000))
                .build();

        when(analyticsService.analyticsSummaryUserIdGet(userId, startDate, endDate)).thenReturn(mockResponse);

        ResponseEntity<SummaryResponse> response = analyticsController
                .analyticsSummaryUserIdGet(userId, startDate, endDate);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void testAnalyticsTopCategoriesUserIdGet() {
        List<CategoryExpenseResponseInner> mockResponse = List.of(
                CategoryExpenseResponseInner.builder().category("Food").totalAmount(BigDecimal.valueOf(500)).build(),
                CategoryExpenseResponseInner.builder().category("Transport")
                        .totalAmount(BigDecimal.valueOf(300)).build()
        );

        when(analyticsService.analyticsTopCategoriesUserIdGet(userId)).thenReturn(mockResponse);

        ResponseEntity<List<CategoryExpenseResponseInner>> response = analyticsController
                .analyticsTopCategoriesUserIdGet(userId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void testAnalyticsTopExpensesUserIdGet() {
        List<TopExpensesResponseInner> mockResponse = List.of(
                TopExpensesResponseInner.builder().description("Restaurant").amount(BigDecimal.valueOf(100)).build(),
                TopExpensesResponseInner.builder().description("Taxi").amount(BigDecimal.valueOf(50)).build()
        );

        when(analyticsService.analyticsTopExpensesUserIdGet(userId, startDate, endDate)).thenReturn(mockResponse);

        ResponseEntity<List<TopExpensesResponseInner>> response = analyticsController
                .analyticsTopExpensesUserIdGet(userId, startDate, endDate);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockResponse, response.getBody());
    }
}
