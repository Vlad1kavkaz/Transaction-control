package org.txn.control.personreg.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.txn.control.personreg.model.Role;
import org.txn.control.personreg.model.RoleCreateRequest;
import org.txn.control.personreg.entities.RoleEntity;
import org.txn.control.personreg.exception.RoleNotFoundException;
import org.txn.control.personreg.mappers.RequestMapper;
import org.txn.control.personreg.mappers.ResponseMapper;
import org.txn.control.personreg.repositories.RoleRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @InjectMocks
    private RoleService roleService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RequestMapper requestMapper;

    @Mock
    private ResponseMapper responseMapper;

    private UUID roleId;
    private RoleCreateRequest roleCreateRequest;
    private RoleEntity roleEntity;
    private Role roleResponse;

    @BeforeEach
    void setUp() {
        roleId = UUID.randomUUID();

        roleCreateRequest = new RoleCreateRequest(RoleCreateRequest.RoleEnum.ADMIN);

        roleEntity = new RoleEntity();
        roleEntity.setId(roleId);
        roleEntity.setRole("ADMIN");

        roleResponse = Role.builder()
                .id(roleId)
                .role(Role.RoleEnum.ADMIN)
                .build();
    }

    @Test
    void createRole_Success() {
        // Arrange
        when(requestMapper.toRoleEntity(roleCreateRequest)).thenReturn(roleEntity);
        when(roleRepository.save(roleEntity)).thenReturn(roleEntity);
        when(responseMapper.toRoleResponse(roleEntity)).thenReturn(roleResponse);

        // Act
        Role result = roleService.create(roleCreateRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(roleId);
        assertThat(result.getRole()).isEqualTo(Role.RoleEnum.ADMIN);

        // Verify interactions
        verify(roleRepository, times(1)).save(roleEntity);
        verify(requestMapper, times(1)).toRoleEntity(roleCreateRequest);
        verify(responseMapper, times(1)).toRoleResponse(roleEntity);
    }

    @Test
    void deleteRole_Success() {
        // Arrange
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(roleEntity));
        doNothing().when(roleRepository).deleteById(roleId);

        // Act
        roleService.delete(roleId);

        // Assert
        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, times(1)).deleteById(roleId);
    }

    @Test
    void deleteRole_ThrowsRoleNotFoundException() {
        // Arrange
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        // Act & Assert
        RoleNotFoundException exception = assertThrows(RoleNotFoundException.class,
                () -> roleService.delete(roleId));

        assertThat(exception.getMessage()).isEqualTo("Role not found for id [%s]".formatted(roleId));

        // Verify that delete was never called
        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, times(0)).deleteById(any());
    }

    @Test
    void createRole_SavesCorrectEntity() {
        // Arrange
        when(requestMapper.toRoleEntity(roleCreateRequest)).thenReturn(roleEntity);
        when(roleRepository.save(roleEntity)).thenReturn(roleEntity);
        when(responseMapper.toRoleResponse(roleEntity)).thenReturn(roleResponse);

        // Act
        roleService.create(roleCreateRequest);

        // Capture the saved entity
        ArgumentCaptor<RoleEntity> captor = ArgumentCaptor.forClass(RoleEntity.class);
        verify(roleRepository).save(captor.capture());

        // Assert
        assertThat(captor.getValue().getRole()).isEqualTo("ADMIN");
    }
}
