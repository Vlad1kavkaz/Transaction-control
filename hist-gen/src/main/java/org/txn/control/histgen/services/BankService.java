package org.txn.control.histgen.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.txn.control.histgen.dictionaries.BankDictionary;
import org.txn.control.histgen.model.Bank;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
@RequiredArgsConstructor
public class BankService {

    private final Queue<Bank> banks = new LinkedList<>();
    private final List<Bank> sendBanks;
    private int count = 0;

    public List<Bank> getAllBanks() {
        List<Bank> bankList = new ArrayList<>(banks);
        sendBanks.addAll(bankList);
        banks.clear();
        return bankList;
    }

    @Scheduled(fixedRate = 4200)
    private void scheduled() {
        if (BankDictionary.BANKS.size() - 1 >= count) {
            banks.add(BankDictionary.BANKS.get(count));
            count++;
        }
    }
}
