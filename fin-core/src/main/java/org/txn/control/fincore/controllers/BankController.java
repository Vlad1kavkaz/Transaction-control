package org.txn.control.fincore.controllers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.txn.control.fincore.api.BanksApiDelegate;
import org.txn.control.fincore.model.Bank;
import org.txn.control.fincore.services.crud.BankService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BankController implements BanksApiDelegate {

    private final BankService bankService;

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Bank>> banksGet(String country) {
        log.info("Getting banks for " + country);
        return ResponseEntity.ok(bankService.findBanks(country));
    }
}
