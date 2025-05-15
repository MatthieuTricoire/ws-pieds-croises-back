package com.crossfit.pieds_croises.repository;

import com.crossfit.pieds_croises.model.PerformanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceHistoryRepository extends JpaRepository<PerformanceHistory, Long> {
}
