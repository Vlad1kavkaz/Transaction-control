package org.txn.control.fincore.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.txn.control.fincore.BaseRepositoryTest;
import org.txn.control.fincore.entities.BankEntity;
import org.txn.control.fincore.entities.IncomeEntity;
import org.txn.control.fincore.entities.PersonEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class IncomeRepositoryTest extends BaseRepositoryTest<IncomeEntity> {

    private final IncomeRepository repository;

    @Autowired
    public IncomeRepositoryTest(TestEntityManager entityManager, IncomeRepository repository) {
        super(entityManager);
        this.repository = repository;
    }

    @Override
    protected IncomeEntity createTestEntity() {
        IncomeEntity incomeEntity = new IncomeEntity();
        incomeEntity.setAmount(BigDecimal.valueOf(5000.00));
        incomeEntity.setDate(ZonedDateTime.now());
        incomeEntity.setDescription("Salary Payment");
        incomeEntity.setUser(createUser());
        incomeEntity.setBank(createBank());
        return incomeEntity;
    }

    private PersonEntity createUser() {
        PersonEntity user = new PersonEntity();
        user.setCreatedAt(ZonedDateTime.now());
        user.setEmail("testuser@example.com");
        user.setPassword("password123");
        user.setUsername("testuser1");
        return entityManager.persistFlushFind(user);
    }

    private BankEntity createBank() {
        BankEntity bank = new BankEntity();
        bank.setCountry("Russia");
        bank.setName("VTB Bank");
        return entityManager.persistFlushFind(bank);
    }

    @Test
    void testSaveIncomeEntity() {
        // Arrange
        IncomeEntity incomeToSave = testEntity;

        // Act
        IncomeEntity savedIncome = repository.save(incomeToSave);

        // Assert
        assertEntity(incomeToSave, savedIncome);
    }

    @Test
    void testFindById_ExistingIncome() {
        // Arrange
        IncomeEntity savedIncome = entityManager.persistFlushFind(testEntity);

        // Act
        Optional<IncomeEntity> foundIncome = repository.findById(savedIncome.getId());

        // Assert
        assertThat(foundIncome).isPresent();
        assertEntity(savedIncome, foundIncome.get());
    }

    @Test
    void testFindById_NonExistingIncome() {
        // Act
        Optional<IncomeEntity> foundIncome = repository.findById(UUID.randomUUID());

        // Assert
        assertThat(foundIncome).isNotPresent();
    }

    @Test
    void testFindFilteredIncomes_NoResults() {
        // Arrange
        createIncome(ZonedDateTime.now().minusDays(10), BigDecimal.valueOf(1000.00));

        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(5);

        // Act
        List<IncomeEntity> incomes = repository.findFilteredIncomes(
                testEntity.getUser().getId(), startDate, endDate, 0, 10
        );

        // Assert
        assertThat(incomes).isEmpty();
    }

    @Test
    void testFindFilteredIncomes_Pagination() {
        // Arrange
        createIncome(ZonedDateTime.now().minusDays(5), BigDecimal.valueOf(3000.00));
        createIncome(ZonedDateTime.now().minusDays(3), BigDecimal.valueOf(4000.00));
        createIncome(ZonedDateTime.now(), BigDecimal.valueOf(5000.00));

        // Act
        List<IncomeEntity> firstPage = repository.findFilteredIncomes(
                testEntity.getUser().getId(), null, null, 0, 2
        );
        List<IncomeEntity> secondPage = repository.findFilteredIncomes(
                testEntity.getUser().getId(), null, null, 1, 2
        );

        // Assert
        assertThat(firstPage).hasSize(2);
        assertThat(secondPage).hasSize(1);
    }

    private void createIncome(ZonedDateTime date, BigDecimal amount) {
        IncomeEntity incomeEntity = new IncomeEntity();
        incomeEntity.setUser(testEntity.getUser());
        incomeEntity.setBank(testEntity.getBank());
        incomeEntity.setAmount(amount);
        incomeEntity.setDate(date);
        incomeEntity.setDescription("Bonus Payment");
        entityManager.persistFlushFind(incomeEntity);
    }

    private void assertEntity(IncomeEntity expected, IncomeEntity actual) {
        assertThat(expected.getAmount()).isEqualTo(actual.getAmount());
        assertThat(expected.getUser()).isEqualTo(actual.getUser());
        assertThat(expected.getBank()).isEqualTo(actual.getBank());
        assertThat(expected.getDate()).isEqualTo(actual.getDate());
        assertThat(expected.getDescription()).isEqualTo(actual.getDescription());
    }
}
