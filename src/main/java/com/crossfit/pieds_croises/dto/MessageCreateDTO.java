package com.crossfit.pieds_croises.dto;

import com.crossfit.pieds_croises.model.Message;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageCreateDTO {
    @NotBlank(message = "Le titre ne doit pas être vide")
    @Size(min = 2, max = 100, message = "Le titre doit contenir entre 2 et 50 caractères")
    private String title;

    @NotBlank(message = "Le contenu ne doit pas être vide")
    private String content;

    @NotNull(message = "Le type de message est obligatoire")
    private Message.MessageType messageType;

    private LocalDate startDate;
    private LocalDate expirationDate;
}
