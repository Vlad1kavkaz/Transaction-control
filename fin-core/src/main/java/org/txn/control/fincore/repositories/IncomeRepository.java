package org.txn.control.fincore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.txn.control.fincore.entities.IncomeEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface IncomeRepository extends JpaRepository<IncomeEntity, UUID> {

    @Query(value = """
        SELECT * FROM incomes i
        WHERE i.person_id = :userId
          AND (:startDate IS NULL OR i.date >= :startDate)
          AND (:endDate IS NULL OR i.date <= :endDate)
        ORDER BY i.date DESC
        LIMIT :size OFFSET :page * :size
        """, nativeQuery = true)
    List<IncomeEntity> findFilteredIncomes(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("page") Integer page,
            @Param("size") Integer size
    );
}
