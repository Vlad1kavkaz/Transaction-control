package org.txn.control.fincore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.txn.control.fincore.entities.PersonEntity;

import java.util.UUID;

@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, UUID> {

    boolean existsById(UUID id);
}
