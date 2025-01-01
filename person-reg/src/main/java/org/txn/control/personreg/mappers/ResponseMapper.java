package org.txn.control.personreg.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.txn.control.fincore.model.Person;
import org.txn.control.fincore.model.Role;
import org.txn.control.personreg.entities.PersonEntity;
import org.txn.control.personreg.entities.RoleEntity;

@Named("responseMapper")
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class ResponseMapper {

    public abstract Role toRoleResponse(RoleEntity roleEntity);

    public abstract Person toPersonResponse(PersonEntity personEntity);
}
