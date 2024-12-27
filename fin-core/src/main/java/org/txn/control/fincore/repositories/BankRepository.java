package org.txn.control.fincore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.txn.control.fincore.entities.BankEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface BankRepository extends JpaRepository<BankEntity, UUID> {

    List<BankEntity> findByCountry(String countryName);
}
