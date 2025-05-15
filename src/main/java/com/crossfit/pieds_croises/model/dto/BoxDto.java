package com.crossfit.pieds_croises.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoxDto {
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;

    private String address;

    @Size(max = 100, message = "City must be less than 100 characters")
    private String city;

    @NotBlank(message = "Zipcode is required")
    @Size(max = 5, message = "Zipcode must be less than 5 characters")
    private String zipcode;

    private String schedule;

}
