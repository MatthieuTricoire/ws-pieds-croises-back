package com.crossfit.pieds_croises.dto;

import com.crossfit.pieds_croises.model.Message;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {

    private Long id;
    private String title;
    private String content;
    private Message.MessageType messageType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDate startDate;
    private LocalDate expirationDate;
    private Long boxId;

}
