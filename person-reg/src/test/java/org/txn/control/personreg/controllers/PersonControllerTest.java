package org.txn.control.personreg.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.txn.control.personreg.model.AssignRoleRequest;
import org.txn.control.personreg.model.Person;
import org.txn.control.personreg.model.PersonCreateRequest;
import org.txn.control.personreg.exception.PersonNotFoundException;
import org.txn.control.personreg.services.PersonService;

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
class PersonControllerTest {

    @Mock
    private PersonService personService;

    @InjectMocks
    private PersonController personController;

    @Test
    void test_createPerson() {
        // Arrange
        PersonCreateRequest request = new PersonCreateRequest(
                "johndoe", "john@example.com",
                "password123", UUID.randomUUID());
        Person person = Person.builder()
                .id(UUID.randomUUID())
                .username("johndoe")
                .build();

        when(personService.createPerson(request)).thenReturn(person);

        // Act
        ResponseEntity<Person> response = personController.createPerson(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(person, response.getBody());

        ArgumentCaptor<PersonCreateRequest> captor = ArgumentCaptor.forClass(PersonCreateRequest.class);
        verify(personService, times(1)).createPerson(captor.capture());
        assertEquals("johndoe", captor.getValue().getUsername());
    }

    @Test
    void test_assignRoleToPerson() {
        // Arrange
        UUID personId = UUID.randomUUID();
        AssignRoleRequest request = new AssignRoleRequest();
        request.setRoleId(UUID.randomUUID());

        // Act
        ResponseEntity<Void> response = personController.assignRoleToPerson(personId, request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());

        verify(personService, times(1)).assignRoleToPerson(personId, request);
    }

    @Test
    void test_deletePerson() {
        // Arrange
        UUID personId = UUID.randomUUID();
        doNothing().when(personService).deletePerson(personId);

        // Act
        ResponseEntity<Void> response = personController.deletePerson(personId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());

        verify(personService, times(1)).deletePerson(personId);
    }

    @Test
    void test_deletePerson_notFound() {
        // Arrange
        UUID personId = UUID.randomUUID();
        doThrow(new PersonNotFoundException("Person not found")).when(personService).deletePerson(personId);

        // Act & Assert
        PersonNotFoundException exception = assertThrows(PersonNotFoundException.class,
                () -> personController.deletePerson(personId));

        assertEquals("Person not found", exception.getMessage());
        verify(personService, times(1)).deletePerson(personId);
    }

    @Test
    void test_getPersonById() {
        // Arrange
        UUID personId = UUID.randomUUID();
        Person person = Person.builder()
                .id(UUID.randomUUID())
                .username("johndoe")
                .build();

        when(personService.getPerson(personId)).thenReturn(person);

        // Act
        ResponseEntity<Person> response = personController.getPersonById(personId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(person, response.getBody());

        verify(personService, times(1)).getPerson(personId);
    }
}
