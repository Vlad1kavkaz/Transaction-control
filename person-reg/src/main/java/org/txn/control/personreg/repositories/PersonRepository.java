package org.txn.control.personreg.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.txn.control.personreg.entities.PersonEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, UUID> {

    Optional<PersonEntity> findByUsernameAndPassword(String username, String password);
}
