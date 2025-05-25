package com.crossfit.pieds_croises.repository;

import com.crossfit.pieds_croises.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByExpirationDateBefore(LocalDate date);

    List<Message> findByStartDateAfter(LocalDate date);
}
