package org.txn.control.histgen.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.txn.control.histgen.model.Bank;
import org.txn.control.histgen.model.Category;
import org.txn.control.histgen.model.Transaction;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final Random rand = new Random();
    private final List<Bank> banks;
    private final List<Category> categories;

    public List<Transaction> getTransactionsForUserId(UUID userId) {
        List<Transaction> transactions = new ArrayList<>();
        int size = rand.nextInt() % 5;
        for (int i = 0; i < size; i++) {
            Transaction.TypeEnum type = rand.nextBoolean() ? Transaction.TypeEnum.EXPENSE : Transaction.TypeEnum.INCOME;

            transactions.add(Transaction.builder()
                    .userId(userId)
                    .bankName(banks.get(rand.nextInt(banks.size())).getName())
                    .type(type)
                    .categoryName(type.equals(Transaction.TypeEnum.EXPENSE)
                            ? categories.get(rand.nextInt(categories.size())).getName()
                            : null)
                    .date(ZonedDateTime.now())
                    .description("Some description")
                    .build());
        }

        return transactions;
    }
}
