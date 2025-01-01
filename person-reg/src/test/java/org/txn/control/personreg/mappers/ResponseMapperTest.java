package org.txn.control.personreg.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.txn.control.fincore.model.Person;
import org.txn.control.fincore.model.Role;
import org.txn.control.personreg.entities.PersonEntity;
import org.txn.control.personreg.entities.RoleEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ResponseMapperTest {

    @InjectMocks
    private ResponseMapper responseMapper = Mappers.getMapper(ResponseMapper.class);

    private UUID roleId;
    private UUID personId;
    private RoleEntity roleEntity;
    private PersonEntity personEntity;

    @BeforeEach
    void setUp() {
        roleId = UUID.randomUUID();
        personId = UUID.randomUUID();

        roleEntity = new RoleEntity();
        roleEntity.setId(roleId);
        roleEntity.setRole("ADMIN");

        personEntity = new PersonEntity();
        personEntity.setId(personId);
        personEntity.setUsername("johndoe");
        personEntity.setEmail("john@example.com");
        personEntity.setRole(roleEntity);
    }

    @Test
    void toRoleResponse_Success() {
        // Act
        Role role = responseMapper.toRoleResponse(roleEntity);

        // Assert
        assertThat(role).isNotNull();
        assertThat(role.getId()).isEqualTo(roleId);
        assertThat(role.getRole()).isEqualTo(Role.RoleEnum.ADMIN);
    }

    @Test
    void toRoleResponse_NullEntity() {
        // Act
        Role role = responseMapper.toRoleResponse(null);

        // Assert
        assertThat(role).isNull();
    }

    @Test
    void toRoleResponse_InvalidRole_ThrowsException() {
        // Arrange
        roleEntity.setRole("INVALID_ROLE");  // Некорректное значение

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            responseMapper.toRoleResponse(roleEntity);
        });
    }

    @Test
    void toPersonResponse_Success() {
        // Act
        Person person = responseMapper.toPersonResponse(personEntity);

        // Assert
        assertThat(person).isNotNull();
        assertThat(person.getId()).isEqualTo(personId);
        assertThat(person.getUsername()).isEqualTo("johndoe");
        assertThat(person.getEmail()).isEqualTo("john@example.com");
        assertThat(person.getRole()).isNotNull();
        assertThat(person.getRole().getRole()).isEqualTo(Role.RoleEnum.ADMIN);
    }

    @Test
    void toPersonResponse_NullEntity() {
        // Act
        Person person = responseMapper.toPersonResponse(null);

        // Assert
        assertThat(person).isNull();
    }

    @Test
    void toPersonResponse_NullRole() {
        // Arrange
        personEntity.setRole(null);

        // Act
        Person person = responseMapper.toPersonResponse(personEntity);

        // Assert
        assertThat(person).isNotNull();
        assertThat(person.getRole()).isNull();
        assertThat(person.getId()).isEqualTo(personId);
    }
}
