package org.txn.control.fincore.services.crud;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.txn.control.fincore.entities.IncomeEntity;
import org.txn.control.fincore.exception.CategoryNotFoundException;
import org.txn.control.fincore.exception.ExpenseNotFoundException;
import org.txn.control.fincore.exception.IncomeNotFoundException;
import org.txn.control.fincore.exception.UserNotFoundException;
import org.txn.control.fincore.feign.clients.MsGenerateClient;
import org.txn.control.fincore.mappers.EntityMapper;
import org.txn.control.fincore.mappers.ResponseMapper;
import org.txn.control.fincore.model.Transaction;
import org.txn.control.fincore.model.TransactionUpdate;
import org.txn.control.fincore.entities.ExpenseEntity;
import org.txn.control.fincore.repositories.CategoryRepository;
import org.txn.control.fincore.repositories.ExpenseRepository;
import org.txn.control.fincore.repositories.IncomeRepository;
import org.txn.control.fincore.repositories.PersonRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {

    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;
    private final PersonRepository personRepository;
    private final CategoryRepository categoryRepository;

    private final MsGenerateClient client;

    private final ResponseMapper responseMapper;
    private final EntityMapper entityMapper;

    public List<Transaction> getTransactions(
            UUID userId,
            String type,
            LocalDate startDate,
            LocalDate endDate,
            Integer page,
            Integer size) {

        if (!checkExistsUser(userId)) {
            throw new UserNotFoundException("User with id [%s] does not exist]".formatted(userId));
        }
        updateTransaction(userId);

        if ("EXPENSE".equalsIgnoreCase(type)) {
            return expenseRepository.findFilteredExpenses(userId, startDate, endDate, page, size).stream()
                    .map(responseMapper::expenseToTransaction)
                    .collect(Collectors.toList());
        } else {
            return incomeRepository.findFilteredIncomes(userId, startDate, endDate, page, size).stream()
                    .map(responseMapper::incomeToTransaction)
                    .collect(Collectors.toList());
        }
    }


    public Transaction getTransactionById(UUID id, String type) {
        if ("EXPENSE".equalsIgnoreCase(type)) {
            ExpenseEntity expenseEntity = expenseRepository.findById(id)
                    .orElseThrow(() -> new ExpenseNotFoundException("Expense not found with id: " + id));
            return responseMapper.expenseToTransaction(expenseEntity);
        } else {
            IncomeEntity incomeEntity = incomeRepository.findById(id)
                    .orElseThrow(() -> new IncomeNotFoundException("Income not found with id: " + id));
            return responseMapper.incomeToTransaction(incomeEntity);
        }
    }

    public void updateTransaction(UUID id, TransactionUpdate transactionUpdate) {
        if ("EXPENSE".equalsIgnoreCase(transactionUpdate.getType().toString())) {
            ExpenseEntity expenseEntity = expenseRepository.findById(id)
                    .orElseThrow(() -> new ExpenseNotFoundException("Expense not found with id: " + id));
            expenseEntity.setAmount(transactionUpdate.getAmount());
            expenseEntity.setCategory(categoryRepository.findById(transactionUpdate.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: "
                            + transactionUpdate.getCategoryId()))
            );
            expenseEntity.setDescription(transactionUpdate.getDescription());
            expenseRepository.save(expenseEntity);
        } else {
            IncomeEntity incomeEntity = incomeRepository.findById(id)
                    .orElseThrow(() -> new IncomeNotFoundException("Income not found with id: " + id));
            incomeEntity.setAmount(transactionUpdate.getAmount());
            incomeEntity.setDescription(transactionUpdate.getDescription());
            incomeRepository.save(incomeEntity);
        }
    }

    public void deleteTransaction(UUID id, String type) {
        if ("EXPENSE".equalsIgnoreCase(type)) {
            if (!expenseRepository.existsById(id)) {
                throw new ExpenseNotFoundException("Expense not found with id: " + id);
            }
            expenseRepository.deleteById(id);
        } else {
            if (!incomeRepository.existsById(id)) {
                throw new IncomeNotFoundException("Income not found with id: " + id);
            }
            incomeRepository.deleteById(id);
        }
    }

    private void updateTransaction(UUID userId) {
        List<Transaction> newTransactionForUserId = client.transaction(userId);
        newTransactionForUserId.forEach(transaction -> {
            if (transaction.getType().equals(Transaction.TypeEnum.EXPENSE)) {
                expenseRepository.save(entityMapper.toExpenseEntity(transaction));
            } else {
                incomeRepository.save(entityMapper.toIncomeEntity(transaction));
            }
        });
    }

    private boolean checkExistsUser(UUID id) {
        return personRepository.existsById(id);
    }
}
