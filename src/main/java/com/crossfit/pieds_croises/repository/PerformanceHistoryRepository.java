package com.crossfit.pieds_croises.repository;

import com.crossfit.pieds_croises.model.PerformanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PerformanceHistoryRepository extends JpaRepository<PerformanceHistory, Long> {

    List<PerformanceHistory> findAllByUserIdOrderByDateAsc(Long userId);

    @Query("SELECT w FROM PerformanceHistory w WHERE w.user.id = :userId AND w.date >= :startDate ORDER BY w.date ASC")
    List<PerformanceHistory> findAllByUserIdForLastXMonths(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate
    );
}
