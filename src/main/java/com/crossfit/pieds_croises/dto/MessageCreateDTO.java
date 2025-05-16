package com.crossfit.pieds_croises.dto;

import com.crossfit.pieds_croises.model.Message;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MessageCreateDTO {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private Message.MessageType messageType;

    private LocalDate startDate;
    private LocalDate expirationDate;
}
