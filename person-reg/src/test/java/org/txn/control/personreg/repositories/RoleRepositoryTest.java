package org.txn.control.personreg.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.txn.control.personreg.BaseRepositoryTest;
import org.txn.control.personreg.entities.RoleEntity;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RoleRepositoryTest extends BaseRepositoryTest<RoleEntity> {

    private final RoleRepository repository;

    @Autowired
    public RoleRepositoryTest(TestEntityManager entityManager, RoleRepository repository) {
        super(entityManager);
        this.repository = repository;
    }

    @Override
    protected RoleEntity createTestEntity() {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRole("USER");
        return roleEntity;
    }

    @Test
    void testSaveRoleEntity() {
        // Arrange
        RoleEntity roleToSave = testEntity;

        // Act
        RoleEntity savedRole = repository.save(roleToSave);

        // Assert
        assertThat(savedRole).isNotNull();
        assertThat(savedRole.getId()).isNotNull();
        assertThat(savedRole.getRole()).isEqualTo(testEntity.getRole());
    }

    @Test
    void testFindById_ExistingId() {
        // Arrange
        RoleEntity savedRole = entityManager.persistFlushFind(testEntity);

        // Act
        Optional<RoleEntity> foundRole = repository.findById(savedRole.getId());

        // Assert
        assertThat(foundRole).isPresent();
        assertThat(foundRole.get().getRole()).isEqualTo(savedRole.getRole());
    }

    @Test
    void testFindById_NonExistingId() {
        // Act
        Optional<RoleEntity> foundRole = repository.findById(UUID.randomUUID());

        // Assert
        assertThat(foundRole).isNotPresent();
    }

    @Test
    void testDeleteRoleEntity() {
        // Arrange
        RoleEntity savedRole = entityManager.persistFlushFind(testEntity);

        // Act
        repository.deleteById(savedRole.getId());
        Optional<RoleEntity> deletedRole = repository.findById(savedRole.getId());

        // Assert
        assertThat(deletedRole).isNotPresent();
    }

    @Test
    void testUpdateRoleEntity() {
        // Arrange
        RoleEntity savedRole = entityManager.persistFlushFind(testEntity);

        // Act
        savedRole.setRole("ADMIN");
        RoleEntity updatedRole = repository.save(savedRole);

        // Assert
        assertThat(updatedRole).isNotNull();
        assertThat(updatedRole.getRole()).isEqualTo("ADMIN");
    }
}
