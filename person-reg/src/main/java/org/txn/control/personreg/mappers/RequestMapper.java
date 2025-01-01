package org.txn.control.personreg.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.txn.control.fincore.model.PersonCreateRequest;
import org.txn.control.fincore.model.RoleCreateRequest;
import org.txn.control.personreg.entities.PersonEntity;
import org.txn.control.personreg.entities.RoleEntity;
import org.txn.control.personreg.exception.RoleNotFoundException;
import org.txn.control.personreg.repositories.RoleRepository;

import java.util.UUID;

@Named("requestMapper")
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class RequestMapper {

    @Autowired
    private RoleRepository roleRepository;

    @Mapping(target = "role", expression = "java(mapRoleFromId(personCreateRequest.getRoleId()))")
    public abstract PersonEntity toPersonEntity(PersonCreateRequest personCreateRequest);

    public abstract RoleEntity toRoleEntity(RoleCreateRequest roleCreateRequest);

    public RoleEntity mapRoleFromId(UUID roleId) {
        if (roleId == null) {
            return null;
        }
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found for id [%s]"
                        .formatted(roleId)));
    }
}
