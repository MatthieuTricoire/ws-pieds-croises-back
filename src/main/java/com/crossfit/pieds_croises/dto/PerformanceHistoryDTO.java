package com.crossfit.pieds_croises.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PerformanceHistoryDTO {

    private Long id;

    @NotNull(message = "La valeur est obligatoire.")
    @Positive(message = "La valeur doit être strictement positive.")
    private Float value;

    @NotNull(message = "La date est obligatoire.")
    @PastOrPresent(message = "La date ne peut pas être dans le futur.")
    private LocalDate date;

    @NotNull(message = "L'exercice est obligatoire.")
    private Long exerciseId;
}
