package org.txn.control.personreg.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.txn.control.personreg.model.Role;
import org.txn.control.personreg.model.RoleCreateRequest;
import org.txn.control.personreg.entities.RoleEntity;
import org.txn.control.personreg.exception.RoleNotFoundException;
import org.txn.control.personreg.mappers.RequestMapper;
import org.txn.control.personreg.mappers.ResponseMapper;
import org.txn.control.personreg.repositories.RoleRepository;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    private final RequestMapper requestMapper;
    private final ResponseMapper responseMapper;

    public Role create(RoleCreateRequest roleCreateRequest) {
        return responseMapper.toRoleResponse(
                roleRepository.save(
                        requestMapper.toRoleEntity(
                                roleCreateRequest)));
    }

    public void delete(UUID id) {
        RoleEntity roleEntity = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found for id [%s]"
                        .formatted(id.toString())));
        roleRepository.deleteById(roleEntity.getId());
    }
}
