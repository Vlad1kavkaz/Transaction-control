package org.txn.control.personreg.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.txn.control.fincore.model.Role;
import org.txn.control.fincore.model.RoleCreateRequest;
import org.txn.control.personreg.exception.RoleNotFoundException;
import org.txn.control.personreg.services.RoleService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    @Test
    void test_createRole() {
        // Arrange
        RoleCreateRequest request = new RoleCreateRequest();
        request.setRole(RoleCreateRequest.RoleEnum.ADMIN);
        Role role = Role.builder()
                .id(UUID.randomUUID())
                .role(Role.RoleEnum.ADMIN)
                .build();

        when(roleService.create(request)).thenReturn(role);

        // Act
        ResponseEntity<Role> response = roleController.createRole(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(role, response.getBody());

        ArgumentCaptor<RoleCreateRequest> captor = ArgumentCaptor.forClass(RoleCreateRequest.class);
        verify(roleService, times(1)).create(captor.capture());
        assertEquals("ADMIN", captor.getValue().getRole().getValue());
    }

    @Test
    void test_deleteRole() {
        // Arrange
        UUID roleId = UUID.randomUUID();
        doNothing().when(roleService).delete(roleId);

        // Act
        ResponseEntity<Void> response = roleController.deleteRole(roleId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());

        verify(roleService, times(1)).delete(roleId);
    }

    @Test
    void test_deleteRole_notFound() {
        // Arrange
        UUID roleId = UUID.randomUUID();
        doThrow(new RoleNotFoundException("Role not found")).when(roleService).delete(roleId);

        // Act & Assert
        RoleNotFoundException exception = assertThrows(RoleNotFoundException.class,
                () -> roleController.deleteRole(roleId));

        assertEquals("Role not found", exception.getMessage());
        verify(roleService, times(1)).delete(roleId);
    }
}
