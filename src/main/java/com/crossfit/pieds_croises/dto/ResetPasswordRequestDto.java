package com.crossfit.pieds_croises.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequestDto {

    @NotBlank(message = "Le token est requis")
    private String resetPasswordToken;

    @NotBlank(message = "Le mot de passe est requis")
    private String newPassword;
}
