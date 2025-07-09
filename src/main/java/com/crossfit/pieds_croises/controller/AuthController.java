package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.LoginRequestDto;
import com.crossfit.pieds_croises.security.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<String> authenticate(@RequestBody LoginRequestDto loginRequestDto) {
        String token = authenticationService.authenticate(
                loginRequestDto.getEmail(),
                loginRequestDto.getPassword()
        );
        return ResponseEntity.ok(token);
    }
}
