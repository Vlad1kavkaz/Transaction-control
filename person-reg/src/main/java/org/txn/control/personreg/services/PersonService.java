package org.txn.control.personreg.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.txn.control.fincore.model.AssignRoleRequest;
import org.txn.control.fincore.model.ExistUser200Response;
import org.txn.control.fincore.model.ExistUserRequest;
import org.txn.control.fincore.model.Person;
import org.txn.control.fincore.model.PersonCreateRequest;
import org.txn.control.personreg.entities.PersonEntity;
import org.txn.control.personreg.entities.RoleEntity;
import org.txn.control.personreg.exception.PersonNotFoundException;
import org.txn.control.personreg.exception.RoleNotFoundException;
import org.txn.control.personreg.mappers.RequestMapper;
import org.txn.control.personreg.mappers.ResponseMapper;
import org.txn.control.personreg.repositories.PersonRepository;
import org.txn.control.personreg.repositories.RoleRepository;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final RoleRepository roleRepository;

    private final RequestMapper requestMapper;
    private final ResponseMapper responseMapper;

    public Person createPerson(PersonCreateRequest createRequest) {
        return responseMapper.toPersonResponse(
                personRepository.save(
                     requestMapper.toPersonEntity(createRequest)
            )
        );
    }

    public void deletePerson(UUID id) {
        personRepository.deleteById(id);
    }

    public Person getPerson(UUID id) {
        return responseMapper.toPersonResponse(
                personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException("Person not found for id: [%s]"
                        .formatted(id.toString()))));
    }

    public void assignRoleToPerson(UUID id, AssignRoleRequest assignRoleRequest) {
        PersonEntity personEntity = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException("Person not found for id: [%s]"
                        .formatted(id.toString())));
        RoleEntity roleEntity = roleRepository.findById(assignRoleRequest.getRoleId())
                .orElseThrow(() -> new RoleNotFoundException("Role not found for id: [%s]"
                        .formatted(assignRoleRequest.getRoleId().toString())));

        personEntity.setRole(roleEntity);
        personRepository.save(personEntity);
    }

    public ExistUser200Response existUser(ExistUserRequest request) {
        return ExistUser200Response.builder()
                .role(personRepository.findByUsernameAndPassword(request.getUsername(), request.getPassword())
                        .orElseThrow(() -> new PersonNotFoundException("Person not found with username [%s]"
                                .formatted(request.getUsername())))
                        .getRole().getRole())
                .build();
    }
}
