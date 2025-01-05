package org.txn.control.personreg.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.txn.control.personreg.model.PersonCreateRequest;
import org.txn.control.personreg.model.RoleCreateRequest;
import org.txn.control.personreg.entities.PersonEntity;
import org.txn.control.personreg.entities.RoleEntity;
import org.txn.control.personreg.exception.RoleNotFoundException;
import org.txn.control.personreg.repositories.RoleRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestMapperTest {

    @InjectMocks
    private RequestMapper requestMapper = Mappers.getMapper(RequestMapper.class);

    @Mock
    private RoleRepository roleRepository;

    private UUID roleId;
    private RoleEntity roleEntity;
    private PersonCreateRequest createRequest;
    private RoleCreateRequest roleCreateRequest;

    @BeforeEach
    void setUp() {
        roleId = UUID.randomUUID();

        roleEntity = new RoleEntity();
        roleEntity.setId(roleId);
        roleEntity.setRole("USER");

        createRequest = new PersonCreateRequest("johndoe", "john@example.com", "password123", roleId);
        roleCreateRequest = new RoleCreateRequest(RoleCreateRequest.RoleEnum.ADMIN);
    }

    @Test
    void toPersonEntity_Success() {
        // Arrange
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(roleEntity));

        // Act
        PersonEntity personEntity = requestMapper.toPersonEntity(createRequest);

        // Assert
        assertThat(personEntity).isNotNull();
        assertThat(personEntity.getUsername()).isEqualTo("johndoe");
        assertThat(personEntity.getEmail()).isEqualTo("john@example.com");
        assertThat(personEntity.getPassword()).isEqualTo("password123");
        assertThat(personEntity.getRole()).isEqualTo(roleEntity);
    }

    @Test
    void toPersonEntity_RoleNotFoundException() {
        // Arrange
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        // Act & Assert
        RoleNotFoundException exception = assertThrows(RoleNotFoundException.class,
                () -> requestMapper.toPersonEntity(createRequest));

        assertThat(exception.getMessage()).isEqualTo("Role not found for id [%s]".formatted(roleId));
    }

    @Test
    void toPersonEntity_NullRoleId() {
        // Arrange
        PersonCreateRequest requestWithoutRole = new PersonCreateRequest("johndoe", "john@example.com", "password123", null);

        // Act
        PersonEntity personEntity = requestMapper.toPersonEntity(requestWithoutRole);

        // Assert
        assertThat(personEntity).isNotNull();
        assertThat(personEntity.getRole()).isNull();
        assertThat(personEntity.getUsername()).isEqualTo("johndoe");
        assertThat(personEntity.getEmail()).isEqualTo("john@example.com");
        assertThat(personEntity.getPassword()).isEqualTo("password123");
    }

    @Test
    void toRoleEntity_Success() {
        // Act
        RoleEntity roleEntity = requestMapper.toRoleEntity(roleCreateRequest);

        // Assert
        assertThat(roleEntity).isNotNull();
        assertThat(roleEntity.getRole()).isEqualTo("ADMIN");
    }

    @Test
    void toRoleEntity_NullRole() {
        // Arrange
        RoleCreateRequest requestWithoutRole = new RoleCreateRequest(null);

        // Act
        RoleEntity roleEntity = requestMapper.toRoleEntity(requestWithoutRole);

        // Assert
        assertThat(roleEntity).isNotNull();
        assertThat(roleEntity.getRole()).isNull();
    }
}
