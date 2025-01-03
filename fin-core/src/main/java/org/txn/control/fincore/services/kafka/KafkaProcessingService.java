package org.txn.control.fincore.services.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.txn.control.fincore.model.Transaction;
import org.txn.control.fincore.model.TransactionsFilterRequestDto;
import org.txn.control.fincore.services.crud.TransactionService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KafkaProcessingService {

    private final TransactionService transactionService;

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
