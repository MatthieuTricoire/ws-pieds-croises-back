package com.crossfit.pieds_croises.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseUpdateDTO {

    @NotNull(message = "L'id doit être renseigner")
    private Long id;

    @NotBlank(message = "Le titre ne doit pas être vide")
    @Size(min = 2, max = 100, message = "Le titre doit contenir entre 2 et 100 caractères")
    private String title;

    @NotBlank(message = "La description ne doit pas être vide")
    private String description;

    @NotNull(message = "La date de début ne doit pas être nulle")
    @Future(message = "La date de début doit être dans le futur")
    private LocalDateTime startDatetime;

    @NotNull(message = "La durée doit être renseignée")
    private short duration;

    @NotNull(message = "Le nombre de personnes max doit être renseigné")
    private Integer personLimit;

    @NotNull(message = "Veuillez renseigner un coach")
    private Long coachId;
}
