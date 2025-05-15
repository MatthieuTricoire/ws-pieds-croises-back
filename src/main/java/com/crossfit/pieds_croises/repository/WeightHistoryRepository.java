package com.crossfit.pieds_croises.repository;

import com.crossfit.pieds_croises.model.WeightHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeightHistoryRepository  extends JpaRepository<WeightHistory, Long> {
}
