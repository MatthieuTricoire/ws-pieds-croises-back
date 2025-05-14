package com.crossfit.pieds_croises.repository;

import com.crossfit.pieds_croises.model.Exercice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Exercice, Long> {
}
