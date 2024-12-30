package org.txn.control.fincore.feign.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.txn.control.fincore.feign.FeignClientConfig;
import org.txn.control.fincore.model.Bank;
import org.txn.control.fincore.model.Category;
import org.txn.control.fincore.model.Transaction;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "ms-generate-system",
        url = "${spring.feign-client.url}",
        configuration = FeignClientConfig.class
)
public interface MsGenerateClient {

    @GetMapping("/transactions/{userId}")
    List<Transaction> transaction(@PathVariable UUID userId);

    @GetMapping("/banks")
    List<Bank> banks();

    @GetMapping("/categories")
    List<Category> categories();
}
