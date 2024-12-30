package org.txn.control.histgen.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.txn.control.histgen.model.Transaction;
import org.txn.control.histgen.services.TransactionService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/api/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/{userId}")
    public List<Transaction> getTransactions(@PathVariable UUID userId) {
        return transactionService.getTransactionsForUserId(userId);
    }
}
