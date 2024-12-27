package org.txn.control.fincore.scheduling;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.txn.control.fincore.mappers.EntityMapper;
import org.txn.control.fincore.model.Bank;
import org.txn.control.fincore.model.Category;
import org.txn.control.fincore.entities.BankEntity;
import org.txn.control.fincore.entities.CategoryEntity;
import org.txn.control.fincore.repositories.BankRepository;
import org.txn.control.fincore.repositories.CategoryRepository;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessingServiceDaoTest {

    @InjectMocks
    private ProcessingServiceDao processingServiceDao;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BankRepository bankRepository;

    @Mock
    private EntityMapper mapper;

    private Bank bank;
    private BankEntity bankEntity;
    private Category category;
    private CategoryEntity categoryEntity;

    @BeforeEach
    void setUp() {
        bank = new Bank();
        bank.setId(UUID.randomUUID());
        bank.setName("Bank Name");
        bank.setCountry("Country");
        bankEntity = new BankEntity(bank.getId(), bank.getName(), bank.getCountry());

        category = new Category();
        category.setId(UUID.randomUUID());
        category.setName("Category Name");
        categoryEntity = new CategoryEntity(category.getId(), category.getName());
    }

    @Test
    void testProcessBanks() {
        // Arrange
        List<Bank> banks = List.of(bank);
        when(mapper.toEntity(bank)).thenReturn(bankEntity);

        // Act
        processingServiceDao.processBanks(banks);

        // Assert
        verify(mapper, times(1)).toEntity(bank);
        verify(bankRepository, times(1)).save(bankEntity);
    }

    @Test
    void testProcessCategories() {
        // Arrange
        List<Category> categories = List.of(category);
        when(mapper.toEntity(category)).thenReturn(categoryEntity);

        // Act
        processingServiceDao.processCategories(categories);

        // Assert
        verify(mapper, times(1)).toEntity(category);
        verify(categoryRepository, times(1)).save(categoryEntity);
    }

    @Test
    void testProcessBanks_WithEmptyList() {
        // Act
        processingServiceDao.processBanks(List.of());

        // Assert
        verifyNoInteractions(mapper);
        verifyNoInteractions(bankRepository);
    }

    @Test
    void testProcessCategories_WithEmptyList() {
        // Act
        processingServiceDao.processCategories(List.of());

        // Assert
        verifyNoInteractions(mapper);
        verifyNoInteractions(categoryRepository);
    }

    @Test
    void testProcessBanks_MultipleBanks() {
        // Arrange
        Bank anotherBank = new Bank();
        bank.setId(UUID.randomUUID());
        bank.setName("Another Bank Name");
        bank.setCountry("Another Country");
        BankEntity anotherBankEntity = new BankEntity(anotherBank.getId(), anotherBank.getName(), anotherBank.getCountry());

        List<Bank> banks = List.of(bank, anotherBank);
        when(mapper.toEntity(bank)).thenReturn(bankEntity);
        when(mapper.toEntity(anotherBank)).thenReturn(anotherBankEntity);

        // Act
        processingServiceDao.processBanks(banks);

        // Assert
        verify(mapper, times(1)).toEntity(bank);
        verify(mapper, times(1)).toEntity(anotherBank);
        verify(bankRepository, times(1)).save(bankEntity);
        verify(bankRepository, times(1)).save(anotherBankEntity);
    }
}
