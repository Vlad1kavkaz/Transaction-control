package org.txn.control.personreg.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.txn.control.personreg.model.AssignRoleRequest;
import org.txn.control.personreg.model.Person;
import org.txn.control.personreg.model.PersonCreateRequest;
import org.txn.control.personreg.entities.PersonEntity;
import org.txn.control.personreg.entities.RoleEntity;
import org.txn.control.personreg.exception.PersonNotFoundException;
import org.txn.control.personreg.exception.RoleNotFoundException;
import org.txn.control.personreg.mappers.RequestMapper;
import org.txn.control.personreg.mappers.ResponseMapper;
import org.txn.control.personreg.repositories.PersonRepository;
import org.txn.control.personreg.repositories.RoleRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @InjectMocks
    private PersonService personService;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RequestMapper requestMapper;

    @Mock
    private ResponseMapper responseMapper;

    private UUID personId;
    private UUID roleId;
    private PersonCreateRequest personCreateRequest;
    private AssignRoleRequest assignRoleRequest;
    private PersonEntity personEntity;
    private RoleEntity roleEntity;
    private Person personResponse;

    @BeforeEach
    void setUp() {
        personId = UUID.randomUUID();
        roleId = UUID.randomUUID();

        personCreateRequest = new PersonCreateRequest("johndoe", "john@example.com",
                "password123", roleId);
        assignRoleRequest = new AssignRoleRequest();
        assignRoleRequest.setRoleId(roleId);

        roleEntity = new RoleEntity();
        roleEntity.setId(roleId);
        roleEntity.setRole("USER");

        personEntity = new PersonEntity();
        personEntity.setId(personId);
        personEntity.setUsername("johndoe");
        personEntity.setEmail("john@example.com");
        personEntity.setRole(roleEntity);

        personResponse = Person.builder()
                .id(personId)
                .username("johndoe")
                .email("john@example.com")
                .build();
    }

    @Test
    void createPerson_Success() {
        // Arrange
        when(requestMapper.toPersonEntity(personCreateRequest)).thenReturn(personEntity);
        when(personRepository.save(personEntity)).thenReturn(personEntity);
        when(responseMapper.toPersonResponse(personEntity)).thenReturn(personResponse);

        // Act
        Person result = personService.createPerson(personCreateRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(personId);
        assertThat(result.getUsername()).isEqualTo("johndoe");

        verify(personRepository, times(1)).save(personEntity);
        verify(requestMapper, times(1)).toPersonEntity(personCreateRequest);
        verify(responseMapper, times(1)).toPersonResponse(personEntity);
    }

    @Test
    void deletePerson_Success() {
        // Arrange
        doNothing().when(personRepository).deleteById(personId);

        // Act
        personService.deletePerson(personId);

        // Assert
        verify(personRepository, times(1)).deleteById(personId);
    }

    @Test
    void getPerson_Success() {
        // Arrange
        when(personRepository.findById(personId)).thenReturn(Optional.of(personEntity));
        when(responseMapper.toPersonResponse(personEntity)).thenReturn(personResponse);

        // Act
        Person result = personService.getPerson(personId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(personId);
        assertThat(result.getUsername()).isEqualTo("johndoe");

        verify(personRepository, times(1)).findById(personId);
        verify(responseMapper, times(1)).toPersonResponse(personEntity);
    }

    @Test
    void getPerson_ThrowsPersonNotFoundException() {
        // Arrange
        when(personRepository.findById(personId)).thenReturn(Optional.empty());

        // Act & Assert
        PersonNotFoundException exception = assertThrows(PersonNotFoundException.class,
                () -> personService.getPerson(personId));

        assertThat(exception.getMessage()).isEqualTo("Person not found for id: [%s]".formatted(personId));
        verify(personRepository, times(1)).findById(personId);
    }

    @Test
    void assignRoleToPerson_Success() {
        // Arrange
        when(personRepository.findById(personId)).thenReturn(Optional.of(personEntity));
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(roleEntity));
        when(personRepository.save(personEntity)).thenReturn(personEntity);

        // Act
        personService.assignRoleToPerson(personId, assignRoleRequest);

        // Assert
        verify(personRepository, times(1)).findById(personId);
        verify(roleRepository, times(1)).findById(roleId);
        verify(personRepository, times(1)).save(personEntity);
    }

    @Test
    void assignRoleToPerson_ThrowsPersonNotFoundException() {
        // Arrange
        when(personRepository.findById(personId)).thenReturn(Optional.empty());

        // Act & Assert
        PersonNotFoundException exception = assertThrows(PersonNotFoundException.class,
                () -> personService.assignRoleToPerson(personId, assignRoleRequest));

        assertThat(exception.getMessage()).isEqualTo("Person not found for id: [%s]".formatted(personId));
        verify(personRepository, times(1)).findById(personId);
        verify(roleRepository, times(0)).findById(any());
    }

    @Test
    void assignRoleToPerson_ThrowsRoleNotFoundException() {
        // Arrange
        when(personRepository.findById(personId)).thenReturn(Optional.of(personEntity));
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        // Act & Assert
        RoleNotFoundException exception = assertThrows(RoleNotFoundException.class,
                () -> personService.assignRoleToPerson(personId, assignRoleRequest));

        assertThat(exception.getMessage()).isEqualTo("Role not found for id: [%s]".formatted(roleId));
        verify(personRepository, times(1)).findById(personId);
        verify(roleRepository, times(1)).findById(roleId);
    }
}
