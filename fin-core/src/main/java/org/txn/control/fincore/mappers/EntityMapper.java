package org.txn.control.fincore.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.txn.control.fincore.entities.BankEntity;
import org.txn.control.fincore.entities.CategoryEntity;
import org.txn.control.fincore.entities.ExpenseEntity;
import org.txn.control.fincore.entities.IncomeEntity;
import org.txn.control.fincore.entities.PersonEntity;
import org.txn.control.fincore.exception.CategoryNotFoundException;
import org.txn.control.fincore.exception.UserNotFoundException;
import org.txn.control.fincore.model.Bank;
import org.txn.control.fincore.model.Category;
import org.txn.control.fincore.model.Transaction;
import org.txn.control.fincore.repositories.CategoryRepository;
import org.txn.control.fincore.repositories.PersonRepository;

import java.util.UUID;

@Named("entityMapper")
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class EntityMapper {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PersonRepository personRepository;

    public abstract BankEntity toEntity(Bank bank);

    public abstract CategoryEntity toEntity(Category category);

    @Mapping(target = "user", expression = "java(userFromId(transaction.getUserId()))")
    public abstract IncomeEntity toIncomeEntity(Transaction transaction);

    @Mapping(target = "user", expression = "java(userFromId(transaction.getUserId()))")
    @Mapping(target = "category", expression = "java(categoryFromId(transaction.getCategoryId()))")
    public abstract ExpenseEntity toExpenseEntity(Transaction transaction);

    public PersonEntity userFromId(UUID id) {
        if (id == null) {
            return null;
        }
        return personRepository.findById(id)
                        .orElseThrow(() -> new UserNotFoundException("User not found for id: [%s]"
                                .formatted(id.toString())));
    }

    public CategoryEntity categoryFromId(UUID id) {
        if (id == null) {
            return null;
        }
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found for id: [%s]"
                        .formatted(id.toString())));
    }
}
