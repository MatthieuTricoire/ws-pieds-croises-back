package com.crossfit.pieds_croises.repository;

import com.crossfit.pieds_croises.model.TypicalWeek;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypicalWeekRepository extends JpaRepository<TypicalWeek, Long> {
    // Custom query methods can be defined here if needed
    // For example, find by name or measure type
}
