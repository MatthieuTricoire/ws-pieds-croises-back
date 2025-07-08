package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.FirstLoginDto;
import com.crossfit.pieds_croises.dto.LoginRequestDto;
import com.crossfit.pieds_croises.dto.UserDto;
import com.crossfit.pieds_croises.security.AuthenticationService;
import com.crossfit.pieds_croises.service.UserService;
import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        UserDto invitedUser = userService.inviteUserByEmail(email);
        return ResponseEntity.status(HttpStatus.CREATED).body(invitedUser);
    }

    @PostMapping("/first-login")
    public ResponseEntity<?> completeFirstLogin(@RequestBody FirstLoginDto dto) {
        userService.completeFirstLogin(dto);
        return ResponseEntity.ok("Mot de passe défini. Vous pouvez maintenant vous connecter.");
    }
}
