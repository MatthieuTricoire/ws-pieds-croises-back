package com.crossfit.pieds_croises.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
public class WeightHistoryDTO {

    private Long id;

    @NotNull(message = "Le poids est obligatoire.")
    @Positive(message = "Le poids doit être strictement positif.")
    private Double weight;

    @NotNull(message = "La date est obligatoire")
    @PastOrPresent(message = "La date ne peut pas être dans le futur")
    private LocalDate date;

}
