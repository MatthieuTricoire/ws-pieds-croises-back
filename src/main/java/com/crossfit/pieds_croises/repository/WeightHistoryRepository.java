package com.crossfit.pieds_croises.repository;

import com.crossfit.pieds_croises.model.WeightHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface WeightHistoryRepository  extends JpaRepository<WeightHistory, Long> {

    List<WeightHistory> findAllByUserIdOrderByDateAsc(Long userId);

    @Query("SELECT w FROM WeightHistory w WHERE w.user.id = :userId AND w.date >= :startDate ORDER BY w.date ASC")
    List<WeightHistory> findAllByUserIdForLastXMonths(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate
    );
}
