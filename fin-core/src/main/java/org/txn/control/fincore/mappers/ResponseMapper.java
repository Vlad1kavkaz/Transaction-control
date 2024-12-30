package org.txn.control.fincore.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.txn.control.fincore.entities.BankEntity;
import org.txn.control.fincore.entities.CategoryEntity;
import org.txn.control.fincore.entities.ExpenseEntity;
import org.txn.control.fincore.entities.IncomeEntity;
import org.txn.control.fincore.model.Bank;
import org.txn.control.fincore.model.Category;
import org.txn.control.fincore.model.Transaction;

@Named("responseMapper")
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class ResponseMapper {

    @Mapping(target = "type", expression = "java(Transaction.TypeEnum.EXPENSE)")
    @Mapping(target = "categoryName", expression = "java(expenseEntity.getCategory() != null ? expenseEntity.getCategory().getName() : null)")
    public abstract Transaction expenseToTransaction(ExpenseEntity expenseEntity);

    @Mapping(target = "type", expression = "java(Transaction.TypeEnum.INCOME)")
    @Mapping(target = "categoryName", ignore = true)
    public abstract Transaction incomeToTransaction(IncomeEntity incomeEntity);

    public abstract Category toResponseCategory(CategoryEntity categoryEntity);

    public abstract Bank toResponseBank(BankEntity bankEntity);
}
