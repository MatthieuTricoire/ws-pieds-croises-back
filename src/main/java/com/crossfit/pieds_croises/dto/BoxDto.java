package com.crossfit.pieds_croises.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoxDto {

  private Long id;

  @NotBlank(message = "Name is required")
  @Size(max = 100, message = "Name must be less than 100 characters")
  private String name;

  @NotBlank(message = "Address is required")
  private String address;

  @NotBlank
  @Size(max = 100, message = "City must be less than 100 characters")
  private String city;

  @NotBlank(message = "Zipcode is required")
  @Size(max = 5, message = "Zipcode must be less than 5 characters")
  private String zipcode;

  private String schedule;

}