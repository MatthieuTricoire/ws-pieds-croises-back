package com.crossfit.pieds_croises.repository;

import com.crossfit.pieds_croises.model.Box;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoxRepository extends JpaRepository<Box, Long> {
    // Custom query methods can be defined here if needed
    // For example, find by name or address
}
