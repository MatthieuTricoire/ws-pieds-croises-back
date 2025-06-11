package com.crossfit.pieds_croises.repository;

import com.crossfit.pieds_croises.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.box.id = :boxId AND m.startDate <= :today AND m.expirationDate >= :today ORDER BY m.expirationDate ASC")
    List<Message> findCurrentMessagesASCByBoxId(@Param("boxId") Long boxId, @Param("today") LocalDate today);

    @Query("SELECT m FROM Message m WHERE m.box.id = :boxId AND m.expirationDate < :today ORDER BY m.expirationDate DESC")
    List<Message> findExpirationMessagesDESCByBoxId(@Param("boxId") Long boxId, @Param("today") LocalDate today);

    @Query("SELECT m FROM Message m WHERE m.box.id = :boxId AND m.startDate > :today ORDER BY m.startDate ASC")
    List<Message> findComingMessagesASCByBoxId(@Param("boxId") Long boxId, @Param("today") LocalDate today);
}
