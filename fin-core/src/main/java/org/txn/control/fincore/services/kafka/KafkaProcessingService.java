package org.txn.control.fincore.services.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.txn.control.fincore.model.Bank;
import org.txn.control.fincore.model.Category;
import org.txn.control.fincore.model.Transaction;
import org.txn.control.fincore.model.TransactionsFilterRequestDto;
import org.txn.control.fincore.services.crud.BankService;
import org.txn.control.fincore.services.crud.CategoryService;
import org.txn.control.fincore.services.crud.TransactionService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaProcessingService {

    private final TransactionService transactionService;
    private final BankService bankService;
    private final CategoryService categoryService;

    public List<Bank> getBanks(String country) {
        return bankService.findBanks(country);
    }

    public List<Category> getCategories() {
        return categoryService.findAll();
    }

    public Transaction getTransaction(UUID transactionId, String type) {
        return transactionService.getTransactionById(transactionId, type);
    }

    public List<Transaction> getTransactions(TransactionsFilterRequestDto requestDto) {
        return transactionService.getTransactions(
                requestDto.getUserId(),
                requestDto.getType().toString(),
                requestDto.getStartDate(),
                requestDto.getEndDate(),
                requestDto.getPage(),
                requestDto.getSize()
        );
    }
}
