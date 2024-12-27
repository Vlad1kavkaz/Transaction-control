package org.txn.control.fincore.services.crud;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.txn.control.fincore.mappers.ResponseMapper;
import org.txn.control.fincore.model.Bank;
import org.txn.control.fincore.repositories.BankRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepository;
    private final ResponseMapper mapper;

    public List<Bank> findBanks(String country) {
        if (country != null && !country.isEmpty()) {
            return bankRepository.findByCountry(country).stream()
                    .map(mapper::toResponseBank)
                    .toList();
        } else {
            return bankRepository.findAll().stream()
                    .map(mapper::toResponseBank)
                    .toList();
        }
    }
}
