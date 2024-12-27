package org.txn.control.fincore.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.txn.control.fincore.BaseRepositoryTest;
import org.txn.control.fincore.entities.PersonEntity;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PersonRepositoryTest extends BaseRepositoryTest<PersonEntity> {

    private final PersonRepository repository;

    @Autowired
    public PersonRepositoryTest(TestEntityManager entityManager, PersonRepository repository) {
        super(entityManager);
        this.repository = repository;
    }

    @Override
    protected PersonEntity createTestEntity() {
        PersonEntity personEntity = new PersonEntity();
        personEntity.setUsername("testuser1");
        personEntity.setEmail("testuser1@example.com");
        personEntity.setPassword("password123");
        personEntity.setCreatedAt(ZonedDateTime.now());
        return personEntity;
    }

    @Test
    void testSavePersonEntity() {
        // Arrange
        PersonEntity personToSave = testEntity;

        // Act
        PersonEntity savedPerson = repository.save(personToSave);

        // Assert
        assertEntity(personToSave, savedPerson);
    }

    @Test
    void testFindById_ExistingPerson() {
        // Arrange
        PersonEntity savedPerson = entityManager.persistFlushFind(testEntity);

        // Act
        Optional<PersonEntity> foundPerson = repository.findById(savedPerson.getId());

        // Assert
        assertThat(foundPerson).isPresent();
        assertEntity(savedPerson, foundPerson.get());
    }

    @Test
    void testFindById_NonExistingPerson() {
        // Act
        Optional<PersonEntity> foundPerson = repository.findById(UUID.randomUUID());

        // Assert
        assertThat(foundPerson).isNotPresent();
    }

    @Test
    void testExistsById_ExistingPerson() {
        // Arrange
        PersonEntity savedPerson = entityManager.persistFlushFind(testEntity);

        // Act
        boolean exists = repository.existsById(savedPerson.getId());

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsById_NonExistingPerson() {
        // Act
        boolean exists = repository.existsById(UUID.randomUUID());

        // Assert
        assertThat(exists).isFalse();
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
        savedPerson.setUsername("updatedUser");
        savedPerson.setEmail("updated@example.com");
        PersonEntity updatedPerson = repository.save(savedPerson);

        // Assert
        assertThat(updatedPerson.getUsername()).isEqualTo("updatedUser");
        assertThat(updatedPerson.getEmail()).isEqualTo("updated@example.com");
    }

    private void assertEntity(PersonEntity expected, PersonEntity actual) {
        assertThat(expected.getUsername()).isEqualTo(actual.getUsername());
        assertThat(expected.getEmail()).isEqualTo(actual.getEmail());
        assertThat(expected.getPassword()).isEqualTo(actual.getPassword());
        assertThat(expected.getCreatedAt()).isEqualTo(actual.getCreatedAt());
    }
}
