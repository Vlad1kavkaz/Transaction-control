package org.txn.control.fincore.scheduling;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.txn.control.fincore.feign.clients.MsGenerateClient;
import org.txn.control.fincore.model.Bank;
import org.txn.control.fincore.model.Category;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TransactionScheduler {

    private final MsGenerateClient client;
    private final ProcessingServiceDao serviceDao;

    @Scheduled(fixedRate = 3000)
    public void scheduleBanks() {
        List<Bank> banks = client.banks();
        serviceDao.processBanks(banks);
    }

    @Scheduled(fixedRate = 3000)
    public void scheduleCategories() {
        List<Category> categories = client.categories();
        serviceDao.processCategories(categories);
    }
}
