package org.txn.control.fincore.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.txn.control.fincore.api.TransactionsApiDelegate;
import org.txn.control.fincore.model.Transaction;
import org.txn.control.fincore.model.TransactionUpdate;
import org.txn.control.fincore.services.crud.TransactionService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TransactionsController implements TransactionsApiDelegate {

    private final TransactionService transactionService;

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Transaction>> transactionsGet(
            UUID userId,
            String type,
            LocalDate startDate,
            LocalDate endDate,
            Integer page,
            Integer size) {

        log.info("Получение списка транзакций для пользователя: {}", userId);
        List<Transaction> transactions = transactionService
                .getTransactions(userId, type, startDate, endDate, page, size);
        return ResponseEntity.ok(transactions);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> transactionsIdDelete(UUID id) {
        log.info("Удаление транзакции с id: {}", id);
        transactionService.deleteTransaction(id, "EXPENSE");
        return ResponseEntity.ok().build();
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Transaction> transactionsIdGet(UUID id) {
        log.info("Получение транзакции с id: {}", id);
        Transaction transaction = transactionService.getTransactionById(id, "EXPENSE");
        return ResponseEntity.ok(transaction);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> transactionsIdPut(UUID id, TransactionUpdate transactionUpdate) {
        log.info("Обновление транзакции с id: {}", id);
        transactionService.updateTransaction(id, transactionUpdate);
        return ResponseEntity.ok().build();
    }
}
