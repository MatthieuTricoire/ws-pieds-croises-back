package com.crossfit.pieds_croises.dto;

import com.crossfit.pieds_croises.model.Message;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private Long id;
    private String title;
    private String content;
    private Message.MessageType messageType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDate startDate;
    private LocalDate expirationDate;
}
