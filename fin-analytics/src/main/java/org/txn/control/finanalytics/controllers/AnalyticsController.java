package org.txn.control.finanalytics.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.txn.control.finanalytics.api.AnalyticsApiDelegate;
import org.txn.control.finanalytics.model.BankIncomeResponseInner;
import org.txn.control.finanalytics.model.BankSummaryResponseInner;
import org.txn.control.finanalytics.model.CategoryExpenseResponseInner;
import org.txn.control.finanalytics.model.IncomeVsExpenseResponse;
import org.txn.control.finanalytics.model.MonthlyHistoryResponseInner;
import org.txn.control.finanalytics.model.PredictionResponseInner;
import org.txn.control.finanalytics.model.SummaryResponse;
import org.txn.control.finanalytics.model.TopExpensesResponseInner;
import org.txn.control.finanalytics.services.AnalyticsService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AnalyticsController implements AnalyticsApiDelegate {

    private final AnalyticsService analyticsService;

    @Override
    public ResponseEntity<List<BankSummaryResponseInner>> analyticsBankSummaryUserIdGet(UUID userId) {
        log.info("Bank summary userId: {}", userId);
        return ResponseEntity.ok(analyticsService.analyticsBankSummaryUserIdGet(userId));
    }

    @Override
    public ResponseEntity<IncomeVsExpenseResponse> analyticsIncomeVsExpenseUserIdGet(UUID userId) {
        log.info("Income vs expense userId: {}", userId);
        return ResponseEntity.ok(analyticsService.analyticsIncomeVsExpenseUserIdGet(userId));
    }

    @Override
    public ResponseEntity<List<BankIncomeResponseInner>> analyticsIncomesByBankUserIdGet(UUID userId) {
        log.info("Income by bank userId: {}", userId);
        return ResponseEntity.ok(analyticsService.analyticsIncomesByBankUserIdGet(userId));
    }

    @Override
    public ResponseEntity<List<MonthlyHistoryResponseInner>> analyticsMonthlyHistoryUserIdGet(UUID userId) {
        log.info("Monthly history userId: {}", userId);
        return ResponseEntity.ok(analyticsService.analyticsMonthlyHistoryUserIdGet(userId));
    }

    @Override
    public ResponseEntity<List<PredictionResponseInner>> analyticsPredictionUserIdGet(UUID userId) {
        log.info("Prediction userId: {}", userId);
        return ResponseEntity.ok(analyticsService.analyticsPredictionUserIdGet(userId));
    }

    @Override
    public ResponseEntity<SummaryResponse> analyticsSummaryUserIdGet(
            UUID userId,
            LocalDate startDate,
            LocalDate endDate) {
        log.info("Summary userId: {}", userId);

        return ResponseEntity.ok(analyticsService.analyticsSummaryUserIdGet(userId, startDate, endDate));
    }

    @Override
    public ResponseEntity<List<CategoryExpenseResponseInner>> analyticsTopCategoriesUserIdGet(UUID userId) {
        log.info("Top categories userId: {}", userId);
        return ResponseEntity.ok(analyticsService.analyticsTopCategoriesUserIdGet(userId));
    }

    @Override
    public ResponseEntity<List<TopExpensesResponseInner>> analyticsTopExpensesUserIdGet(
            UUID userId,
            LocalDate startDate,
            LocalDate endDate) {

        log.info("Top expenses userId: {}", userId);
        return ResponseEntity.ok(analyticsService.analyticsTopExpensesUserIdGet(userId, startDate, endDate));
    }
}