package com.crossfit.pieds_croises.dto;

import java.util.Set;

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
public class AuthUserDto {

  private Long id;

  private String firstname;

  private String lastname;

  private String email;

  private Set<String> roles;

}
