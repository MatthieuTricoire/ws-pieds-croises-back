package com.crossfit.pieds_croises.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoxInfoDTO {
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Size(max = 20, message = "Phone number must be less than 20 characters")
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must be less than 255 characters")
    private String address;

    @NotBlank
    @Size(max = 100, message = "City must be less than 100 characters")
    private String city;

    @NotBlank(message = "Zipcode is required")
    @Size(max = 5, message = "Zipcode must be less than 5 characters")
    private String zipcode;

    @NotBlank(message = "Schedule is required")
    private String schedule;
}