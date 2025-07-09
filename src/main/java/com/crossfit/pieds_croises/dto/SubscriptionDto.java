package com.crossfit.pieds_croises.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionDto {
    @NotBlank(message = "Le nom de l'abonnement ne doit pas être vide")
    @Size(max = 100, message = "Le nom de l'abonnement ne doit pas dépasser 100 caractères")
    private String name;

    @Min(value = 0, message = "Le prix de l'abonnement doit être supérieur ou égal à 0")
    private int price;

    @Min(value = 0, message = "Le nombre de séances par semaine doit être supérieur ou égal à 0")
    private int sessionPerWeek;

    @Min(value = 1, message = "La durée de l'abonnement doit être supérieure ou égale à 1 jour")
    private short duration;

    private String terminationConditions;

    @NotNull(message = "L'ID de la box ne doit pas être nul")
    private Long boxId;

    private int freezeDaysAllowed;
}
