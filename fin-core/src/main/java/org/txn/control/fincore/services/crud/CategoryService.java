package org.txn.control.fincore.services.crud;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.txn.control.fincore.mappers.ResponseMapper;
import org.txn.control.fincore.model.Category;
import org.txn.control.fincore.repositories.CategoryRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    public final CategoryRepository categoryRepository;
    public final ResponseMapper mapper;

    public List<Category> findAll() {
        return categoryRepository.findAll().stream()
                .map(mapper::toResponseCategory)
                .toList();
    }
}
