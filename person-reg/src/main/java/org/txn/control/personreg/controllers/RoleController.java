package org.txn.control.personreg.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.txn.control.personreg.api.RolesApiDelegate;
import org.txn.control.personreg.model.Role;
import org.txn.control.personreg.model.RoleCreateRequest;
import org.txn.control.personreg.services.RoleService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RoleController implements RolesApiDelegate {

    private final RoleService roleService;

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Role> createRole(RoleCreateRequest roleCreateRequest) {
        log.info("Creating role: {}", roleCreateRequest);
        return ResponseEntity.ok(roleService.create(roleCreateRequest));
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> deleteRole(UUID id) {
        log.info("Deleting role: {}", id);
        roleService.delete(id);
        return ResponseEntity.ok().build();
    }
}
