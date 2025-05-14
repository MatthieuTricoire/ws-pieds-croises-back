package com.crossfit.pieds_croises.repository;

import com.crossfit.pieds_croises.model.Exercice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypicalCourseRepository extends JpaRepository<Exercice, Long> {
}
