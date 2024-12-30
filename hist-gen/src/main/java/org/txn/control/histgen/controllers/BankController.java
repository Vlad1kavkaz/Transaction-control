package org.txn.control.histgen.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.txn.control.histgen.model.Bank;
import org.txn.control.histgen.services.BankService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/api/banks")
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;

    @GetMapping("")
    public List<Bank> getAllBanks() {
        log.info("getAllBanks");
        return bankService.getAllBanks();
    }
}
