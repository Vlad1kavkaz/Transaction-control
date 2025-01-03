package org.txn.control.finanalytics.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.txn.control.finanalytics.model.BankIncomeResponseInner;
import org.txn.control.finanalytics.model.BankSummaryResponseInner;
import org.txn.control.finanalytics.model.CategoryExpenseResponseInner;
import org.txn.control.finanalytics.model.IncomeVsExpenseResponse;
import org.txn.control.finanalytics.model.MonthlyHistoryResponseInner;
import org.txn.control.finanalytics.model.PredictionResponseInner;
import org.txn.control.finanalytics.model.SummaryResponse;
import org.txn.control.finanalytics.model.TopExpensesResponseInner;
import org.txn.control.finanalytics.model.Transaction;
import org.txn.control.finanalytics.model.TransactionsFilterRequestDto;
import org.txn.control.finanalytics.services.kafka.ProducerService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    @Value("${spring.kafka.topics.transactions-topic-in}")
    private String TRANSACTION_TOPIC;

    private final ProducerService producerService;

    private final ConcurrentHashMap<String, CompletableFuture<List<Transaction>>> pendingRequests =
            new ConcurrentHashMap<>();

    public SummaryResponse analyticsSummaryUserIdGet(UUID userId, LocalDate startDate, LocalDate endDate) {
        return processAnalyticsRequest(userId, startDate, endDate, null, transactions -> {
            Pair<BigDecimal, BigDecimal> totalAmounts = calculateTotalIncomeExpense(transactions);
            BigDecimal totalIncome = totalAmounts.getLeft();
            BigDecimal totalExpense = totalAmounts.getRight();
            return SummaryResponse.builder()
                    .userId(userId)
                    .totalIncome(totalIncome)
                    .totalExpense(totalExpense)
                    .balance(totalIncome.subtract(totalExpense).abs())
                    .build();
        });
    }

    public List<CategoryExpenseResponseInner> analyticsTopCategoriesUserIdGet(UUID userId) {
        return processAnalyticsRequest(userId, null, null, Transaction.TypeEnum.EXPENSE.getValue(), transactions -> {
            List<CategoryExpenseResponseInner> response = new ArrayList<>();
            for (Transaction transaction : transactions) {
                Optional<CategoryExpenseResponseInner> existingCategory = response.stream()
                        .filter(category -> category.getCategory().equals(transaction.getCategoryName()))
                        .findFirst();
                existingCategory.ifPresentOrElse(
                        category -> category.setTotalAmount(category.getTotalAmount().add(transaction.getAmount())),
                        () -> response.add(CategoryExpenseResponseInner.builder()
                                .category(transaction.getCategoryName())
                                .totalAmount(transaction.getAmount())
                                .build()));
            }
            return response;
        });
    }

    public List<BankIncomeResponseInner> analyticsIncomesByBankUserIdGet(UUID userId) {
        return processAnalyticsRequest(userId, null, null, Transaction.TypeEnum.INCOME.getValue(), transactions -> {
            List<BankIncomeResponseInner> response = new ArrayList<>();
            for (Transaction transaction : transactions) {
                Optional<BankIncomeResponseInner> existingBank = response.stream()
                        .filter(bank -> bank.getBankName().equals(transaction.getBankName()))
                        .findFirst();
                existingBank.ifPresentOrElse(
                        bank -> bank.setTotalIncome(bank.getTotalIncome().add(transaction.getAmount())),
                        () -> response.add(BankIncomeResponseInner.builder()
                                .bankName(transaction.getBankName())
                                .totalIncome(transaction.getAmount())
                                .build()));
            }
            return response;
        });
    }

    public List<TopExpensesResponseInner> analyticsTopExpensesUserIdGet(
            UUID userId,
            LocalDate startDate,
            LocalDate endDate) {

        return processAnalyticsRequest(
                userId, startDate, endDate,
                Transaction.TypeEnum.EXPENSE.getValue(), transactions ->
                transactions.stream()
                        .sorted(Comparator.comparing(Transaction::getAmount).reversed())
                        .limit(5)
                        .map(transaction -> TopExpensesResponseInner.builder()
                                .amount(transaction.getAmount())
                                .date(transaction.getDate().toLocalDate())
                                .description(transaction.getDescription())
                                .build())
                        .toList());
    }

    public IncomeVsExpenseResponse analyticsIncomeVsExpenseUserIdGet(UUID userId) {
        return processAnalyticsRequest(userId, null, null, null, transactions -> {
            Pair<BigDecimal, BigDecimal> totalAmounts = calculateTotalIncomeExpense(transactions);
            return IncomeVsExpenseResponse.builder()
                    .expense(totalAmounts.getRight())
                    .income(totalAmounts.getLeft())
                    .build();
        });
    }

    public List<MonthlyHistoryResponseInner> analyticsMonthlyHistoryUserIdGet(UUID userId) {
        return processAnalyticsRequest(userId, null, null, null, transactions -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            Map<String, List<Transaction>> transactionsByMonth = transactions.stream()
                    .collect(Collectors.groupingBy(t -> t.getDate().format(formatter)));
            return transactionsByMonth.entrySet().stream()
                    .map(entry -> {
                        String month = entry.getKey();
                        List<Transaction> monthlyTransactions = entry.getValue();
                        Pair<BigDecimal, BigDecimal> totals = calculateTotalIncomeExpense(monthlyTransactions);
                        return MonthlyHistoryResponseInner.builder()
                                .month(month)
                                .income(totals.getLeft())
                                .expense(totals.getRight())
                                .build();
                    })
                    .collect(Collectors.toList());
        });
    }

    public List<BankSummaryResponseInner> analyticsBankSummaryUserIdGet(UUID userId) {
        return processAnalyticsRequest(userId, null, null, null, transactions -> {
            Map<String, List<Transaction>> transactionsByBank = transactions.stream()
                    .collect(Collectors.groupingBy(Transaction::getBankName));
            return transactionsByBank.entrySet().stream()
                    .map(entry -> {
                        String bankName = entry.getKey();
                        List<Transaction> bankTransactions = entry.getValue();
                        Pair<BigDecimal, BigDecimal> totals = calculateTotalIncomeExpense(bankTransactions);
                        return BankSummaryResponseInner.builder()
                                .bankName(bankName)
                                .income(totals.getLeft())
                                .expense(totals.getRight())
                                .build();
                    })
                    .collect(Collectors.toList());
        });
    }

    public List<PredictionResponseInner> analyticsPredictionUserIdGet(UUID userId) {
        return processAnalyticsRequest(userId, null, null, Transaction.TypeEnum.EXPENSE.getValue(), transactions -> {
            Map<String, List<Transaction>> transactionsByCategory = transactions.stream()
                    .collect(Collectors.groupingBy(Transaction::getCategoryName));
            return transactionsByCategory.entrySet().stream()
                    .map(entry -> {
                        String category = entry.getKey();
                        List<Transaction> categoryTransactions = entry.getValue();
                        BigDecimal predictedAmount = predictNextMonth(categoryTransactions);
                        return PredictionResponseInner.builder()
                                .category(category)
                                .predictedAmount(predictedAmount)
                                .build();
                    })
                    .collect(Collectors.toList());
        });
    }

    public void completeRequest(String correlationId, List<Transaction> transactions) {
        CompletableFuture<List<Transaction>> future = pendingRequests.remove(correlationId);
        if (future != null) {
            future.complete(transactions);
        } else {
            log.warn("No pending request transaction found for correlationId: {}", correlationId);
        }
    }

    private Pair<BigDecimal, BigDecimal> calculateTotalIncomeExpense(List<Transaction> transactions) {
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        for (Transaction transaction : transactions) {
            if (transaction.getType().equals(Transaction.TypeEnum.INCOME)) {
                totalIncome = totalIncome.add(transaction.getAmount());
            } else {
                totalExpense = totalExpense.add(transaction.getAmount());
            }
        }
        return Pair.of(totalIncome, totalExpense);
    }

    private BigDecimal predictNextMonth(List<Transaction> transactions) {
        Map<String, BigDecimal> monthlyTotals = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));
        List<BigDecimal> lastThreeMonths = monthlyTotals.values().stream()
                .sorted(Comparator.reverseOrder())
                .limit(3)
                .toList();
        if (lastThreeMonths.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = lastThreeMonths.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(BigDecimal.valueOf(lastThreeMonths.size()), RoundingMode.HALF_UP);
    }

    private Pair<String, CompletableFuture<List<Transaction>>> buildAndSendRequest(
            UUID userId,
            LocalDate startDate,
            LocalDate endDate,
            String type) {

        String correlationId = UUID.randomUUID().toString();

        TransactionsFilterRequestDto request = new TransactionsFilterRequestDto();
        request.setUserId(userId);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setType(type == null ? null : TransactionsFilterRequestDto.TypeEnum.valueOf(type));

        CompletableFuture<List<Transaction>> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);
        producerService.sendRequest(TRANSACTION_TOPIC, correlationId, request);

        return Pair.of(correlationId, future);
    }

    private <T> T processAnalyticsRequest(
            UUID userId,
            LocalDate startDate,
            LocalDate endDate,
            String type, Function<List<Transaction>, T> processor) {

        Pair<String, CompletableFuture<List<Transaction>>> request =
                buildAndSendRequest(userId, startDate, endDate, type);

        String correlationId = request.getLeft();
        CompletableFuture<List<Transaction>> future = request.getRight();

        try {
            List<Transaction> transactions = future.get();
            return processor.apply(transactions);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to get response from Kafka", e);
        } finally {
            pendingRequests.remove(correlationId);
        }
    }
}