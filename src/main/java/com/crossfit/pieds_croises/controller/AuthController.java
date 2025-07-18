package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.AuthUserDto;
import com.crossfit.pieds_croises.dto.FirstLoginDto;
import com.crossfit.pieds_croises.dto.ForgotPasswordRequestDto;
import com.crossfit.pieds_croises.dto.LoginRequestDto;
import com.crossfit.pieds_croises.dto.ResetPasswordRequestDto;
import com.crossfit.pieds_croises.mapper.AuthUserMapper;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.repository.UserRepository;
import com.crossfit.pieds_croises.security.AuthenticationService;
import com.crossfit.pieds_croises.security.JwtCookieService;
import com.crossfit.pieds_croises.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
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
  private final UserService userService;
  private final JwtCookieService jwtCookieService;
  private final UserRepository userRepository;
  private final AuthUserMapper authUserMapper;

  @GetMapping("/check")
  public ResponseEntity<?> checkAuthentication(Authentication authentication) {
    if (authentication != null && authentication.isAuthenticated()) {
      return ResponseEntity.ok(Map.of("message", "Utilisateur authentifié"));
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  @GetMapping("/me")
  public ResponseEntity<AuthUserDto> getCurrentUser(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    String email = authentication.getName();
    User user = userRepository.findByEmail(email).orElseThrow();
    return ResponseEntity.ok(
        authUserMapper.convertToDTO(user));
  }

  @PostMapping("/login")
  public ResponseEntity<?> authenticate(@RequestBody LoginRequestDto loginRequestDto,
      HttpServletResponse response) {
    String token = authenticationService.authenticate(
        loginRequestDto.getEmail(),
        loginRequestDto.getPassword());

    jwtCookieService.addJwtCookie(response, token);

    return ResponseEntity.ok().body(Map.of("message", "Connexion réussie"));
  }

  @PostMapping("/register")
  public ResponseEntity<?> completeFirstLogin(@RequestBody FirstLoginDto dto) {
    userService.completeFirstLogin(dto);
    return ResponseEntity.ok("Mot de passe défini. Vous pouvez maintenant vous connecter.");
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto dto) {
    authenticationService.handleForgotPassword(dto.getEmail());
    return ResponseEntity.ok("Si cet email existe, un lien de réinitialisation a été envoyé.");
  }

  @PostMapping("/reset-password")
  public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
    authenticationService.resetPassword(request.getResetPasswordToken(), request.getNewPassword());
    return ResponseEntity.ok("Mot de passe réinitialisé avec succès.");
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletResponse response) {
    jwtCookieService.removeJwtCookie(response);
    return ResponseEntity.ok().body(Map.of("message", "Déconnexion réussie"));
  }
}
