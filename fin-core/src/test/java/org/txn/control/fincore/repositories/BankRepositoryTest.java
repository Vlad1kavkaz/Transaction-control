package org.txn.control.fincore.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.txn.control.fincore.BaseRepositoryTest;
import org.txn.control.fincore.entities.BankEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BankRepositoryTest extends BaseRepositoryTest<BankEntity> {

    private final BankRepository repository;

    @Autowired
    public BankRepositoryTest(TestEntityManager entityManager, BankRepository repository) {
        super(entityManager);
        this.repository = repository;
    }

    @Override
    protected BankEntity createTestEntity() {
        BankEntity bankEntity = new BankEntity();
        bankEntity.setName("Alpha Bank");
        bankEntity.setCountry("Russia");
        return bankEntity;
    }

    @Test
    void testSaveBankEntity() {
        // Arrange
        BankEntity bankToSave = testEntity;

        // Act
        BankEntity savedBank = repository.save(bankToSave);

        // Assert
        assertThat(savedBank).isNotNull();
        assertThat(savedBank.getId()).isNotNull();
        assertThat(savedBank.getName()).isEqualTo(testEntity.getName());
        assertThat(savedBank.getCountry()).isEqualTo(testEntity.getCountry());
    }

    @Test
    void testFindByCountry_ExistingCountry() {
        // Arrange
        BankEntity bank1 = new BankEntity(null, "Alpha Bank", "Russia");
        BankEntity bank2 = new BankEntity(null, "Beta Bank", "Russia");
        entityManager.persistFlushFind(bank1);
        entityManager.persistFlushFind(bank2);

        // Act
        List<BankEntity> foundBanks = repository.findByCountry("Russia");

        // Assert
        assertThat(foundBanks).hasSize(2);
        assertThat(foundBanks)
                .extracting(BankEntity::getName)
                .containsExactlyInAnyOrder("Alpha Bank", "Beta Bank");
    }

    @Test
    void testFindByCountry_NonExistingCountry() {
        // Act
        List<BankEntity> foundBanks = repository.findByCountry("USA");

        // Assert
        assertThat(foundBanks).isEmpty();
    }

    @Test
    void testFindById_ExistingId() {
        // Arrange
        BankEntity savedBank = entityManager.persistFlushFind(testEntity);

        // Act
        Optional<BankEntity> foundBank = repository.findById(savedBank.getId());

        // Assert
        assertThat(foundBank).isPresent();
        assertThat(foundBank.get().getName()).isEqualTo(savedBank.getName());
        assertThat(foundBank.get().getCountry()).isEqualTo(savedBank.getCountry());
    }

    @Test
    void testFindById_NonExistingId() {
        // Act
        Optional<BankEntity> foundBank = repository.findById(UUID.randomUUID());

        // Assert
        assertThat(foundBank).isNotPresent();
    }

    @Test
    void testDeleteBankEntity() {
        // Arrange
        BankEntity savedBank = entityManager.persistFlushFind(testEntity);

        // Act
        repository.deleteById(savedBank.getId());
        Optional<BankEntity> deletedBank = repository.findById(savedBank.getId());

        // Assert
        assertThat(deletedBank).isNotPresent();
    }
}
