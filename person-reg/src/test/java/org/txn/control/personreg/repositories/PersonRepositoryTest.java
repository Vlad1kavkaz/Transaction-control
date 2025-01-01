package org.txn.control.personreg.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.txn.control.personreg.BaseRepositoryTest;
import org.txn.control.personreg.entities.PersonEntity;
import org.txn.control.personreg.entities.RoleEntity;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PersonRepositoryTest extends BaseRepositoryTest<PersonEntity> {

    private final PersonRepository repository;

    @Autowired
    public PersonRepositoryTest(TestEntityManager entityManager, PersonRepository repository) {
        super(entityManager);
        this.repository = repository;
    }

    @Override
    protected PersonEntity createTestEntity() {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRole("USER");
        entityManager.persist(roleEntity);  // Сначала сохраняем роль

        PersonEntity personEntity = new PersonEntity();
        personEntity.setUsername("johndoe");
        personEntity.setEmail("john@example.com");
        personEntity.setPassword("password123");
        personEntity.setRole(roleEntity);  // Привязка роли к пользователю
        return personEntity;
    }

    @Test
    void testSavePersonEntity() {
        // Arrange
        PersonEntity personToSave = testEntity;

        // Act
        PersonEntity savedPerson = repository.save(personToSave);

        // Assert
        assertThat(savedPerson).isNotNull();
        assertThat(savedPerson.getId()).isNotNull();
        assertThat(savedPerson.getUsername()).isEqualTo(testEntity.getUsername());
        assertThat(savedPerson.getEmail()).isEqualTo(testEntity.getEmail());
        assertThat(savedPerson.getRole().getRole()).isEqualTo("USER");
    }

    @Test
    void testFindById_ExistingId() {
        // Arrange
        PersonEntity savedPerson = entityManager.persistFlushFind(testEntity);

        // Act
        Optional<PersonEntity> foundPerson = repository.findById(savedPerson.getId());

        // Assert
        assertThat(foundPerson).isPresent();
        assertThat(foundPerson.get().getUsername()).isEqualTo(savedPerson.getUsername());
        assertThat(foundPerson.get().getEmail()).isEqualTo(savedPerson.getEmail());
        assertThat(foundPerson.get().getRole().getRole()).isEqualTo("USER");
    }

    @Test
    void testFindById_NonExistingId() {
        // Act
        Optional<PersonEntity> foundPerson = repository.findById(UUID.randomUUID());

        // Assert
        assertThat(foundPerson).isNotPresent();
    }

    @Test
    void testDeletePersonEntity() {
        // Arrange
        PersonEntity savedPerson = entityManager.persistFlushFind(testEntity);

        // Act
        repository.deleteById(savedPerson.getId());
        Optional<PersonEntity> deletedPerson = repository.findById(savedPerson.getId());

        // Assert
        assertThat(deletedPerson).isNotPresent();
    }

    @Test
    void testUpdatePersonEntity() {
        // Arrange
        PersonEntity savedPerson = entityManager.persistFlushFind(testEntity);

        // Act
        savedPerson.setEmail("updated@example.com");
        PersonEntity updatedPerson = repository.save(savedPerson);

        // Assert
        assertThat(updatedPerson).isNotNull();
        assertThat(updatedPerson.getEmail()).isEqualTo("updated@example.com");
    }
}
