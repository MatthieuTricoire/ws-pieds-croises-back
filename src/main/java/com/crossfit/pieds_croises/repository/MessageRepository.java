package com.crossfit.pieds_croises.repository;

import com.crossfit.pieds_croises.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
