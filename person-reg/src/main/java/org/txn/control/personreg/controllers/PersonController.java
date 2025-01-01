package org.txn.control.personreg.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.txn.control.fincore.api.PersonApiDelegate;
import org.txn.control.fincore.model.AssignRoleRequest;
import org.txn.control.fincore.model.Person;
import org.txn.control.fincore.model.PersonCreateRequest;
import org.txn.control.personreg.services.PersonService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PersonController implements PersonApiDelegate {

    private final PersonService personService;

    @Override
    public ResponseEntity<Void> assignRoleToPerson(UUID id, AssignRoleRequest assignRoleRequest) {
        log.info("Assign role {} to person {}", assignRoleRequest, id);
        personService.assignRoleToPerson(id, assignRoleRequest);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Person> createPerson(PersonCreateRequest personCreateRequest) {
        log.info("Create person {}", personCreateRequest);
        return ResponseEntity.ok(personService.createPerson(personCreateRequest));
    }

    @Override
    public ResponseEntity<Void> deletePerson(UUID id) {
        log.info("Delete person {}", id);
        personService.deletePerson(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Person> getPersonById(UUID id) {
        log.info("Get person {}", id);
        return ResponseEntity.ok(personService.getPerson(id));
    }
}
