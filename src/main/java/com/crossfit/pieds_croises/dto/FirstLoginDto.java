package com.crossfit.pieds_croises.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FirstLoginDto {
    private String token;
    private String password;
}
