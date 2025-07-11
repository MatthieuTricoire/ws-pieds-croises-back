package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.FirstLoginDto;
import com.crossfit.pieds_croises.dto.LoginRequestDto;
import com.crossfit.pieds_croises.security.AuthenticationService;
import com.crossfit.pieds_croises.service.UserService;
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
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> authenticate(@RequestBody LoginRequestDto loginRequestDto) {
        String token = authenticationService.authenticate(
                loginRequestDto.getEmail(),
                loginRequestDto.getPassword()
        );
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<?> completeFirstLogin(@RequestBody FirstLoginDto dto) {
        userService.completeFirstLogin(dto);
        return ResponseEntity.ok("Mot de passe défini. Vous pouvez maintenant vous connecter.");
    }
}
