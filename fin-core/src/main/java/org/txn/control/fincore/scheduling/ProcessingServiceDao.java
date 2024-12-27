package org.txn.control.fincore.scheduling;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.txn.control.fincore.mappers.EntityMapper;
import org.txn.control.fincore.model.Bank;
import org.txn.control.fincore.model.Category;
import org.txn.control.fincore.repositories.BankRepository;
import org.txn.control.fincore.repositories.CategoryRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProcessingServiceDao {

    private final CategoryRepository categoryRepository;
    private final BankRepository bankRepository;

    private final EntityMapper mapper;

    public void processBanks(List<Bank> banks) {
        banks.stream()
                .map(mapper::toEntity)
                .forEach(bankRepository::save);
    }

    public void processCategories(List<Category> categories) {
        categories.stream()
                .map(mapper::toEntity)
                .forEach(categoryRepository::save);
    }
}
