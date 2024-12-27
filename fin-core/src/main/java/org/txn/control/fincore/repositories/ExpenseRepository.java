package org.txn.control.fincore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.txn.control.fincore.entities.ExpenseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, UUID> {

    @Query(value = """
        SELECT * FROM expenses e
        WHERE e.person_id = :userId
          AND (:startDate IS NULL OR e.date >= :startDate)
          AND (:endDate IS NULL OR e.date <= :endDate)
        ORDER BY e.date DESC
        LIMIT :size OFFSET :page * :size
        """, nativeQuery = true)
    List<ExpenseEntity> findFilteredExpenses(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("page") Integer page,
            @Param("size") Integer size
    );
}
