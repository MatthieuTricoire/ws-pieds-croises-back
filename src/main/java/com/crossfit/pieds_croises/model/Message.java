package com.crossfit.pieds_croises.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column( length = 100,nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT",nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", columnDefinition = "ENUM('INFORMATION', 'ALERT', 'EVENT', 'REMINDER')", nullable = false)
    private Message.MessageType messageType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "start_date")
    LocalDate startDate;

    @Column(name = "expiration_date")
    LocalDate expirationDate;

    public enum MessageType{
        INFORMATION,
        ALERT,
        EVENT,
        REMINDER
    }
}
