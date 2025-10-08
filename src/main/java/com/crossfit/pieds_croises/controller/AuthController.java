package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.FirstLoginDto;
import com.crossfit.pieds_croises.dto.ForgotPasswordRequestDto;
import com.crossfit.pieds_croises.dto.LoginRequestDto;
import com.crossfit.pieds_croises.dto.ResetPasswordRequestDto;
import com.crossfit.pieds_croises.dto.UserDto;
import com.crossfit.pieds_croises.mapper.UserMapper;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.repository.UserRepository;
import com.crossfit.pieds_croises.security.AuthenticationService;
import jakarta.validation.Valid;
import com.crossfit.pieds_croises.security.JwtCookieService;
import com.crossfit.pieds_croises.security.JwtService;
import com.crossfit.pieds_croises.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Gestion de l'authentification et des sessions utilisateur")
public class AuthController {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final JwtCookieService jwtCookieService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserMapper UserMapper;


    @GetMapping("/check")
    @Operation(
        summary = "Vérifier l'authentification",
        description = "Vérifie si l'utilisateur est authentifié via le token dans les cookies."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statut d'authentification retourné"),
        @ApiResponse(responseCode = "500", description = "Erreur serveur", content = @Content)
    })
    public ResponseEntity<Boolean> checkAuth(
        @Parameter(hidden = true) HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        boolean isAuthenticated = false;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    String tokenValue = cookie.getValue();
                    try {
                        if (jwtService.validateJwtToken(tokenValue)) {
                            String email = jwtService.getEmailFromToken(tokenValue);
                            Optional<User> optionalUser = userRepository.findByEmail(email);
                            if (optionalUser.isPresent() && optionalUser.get().isEnabled()) {
                                isAuthenticated = true;
                                logger.info("[/auth/check] Authentification OK pour {}", email);
                            } else {
                                logger.warn("[/auth/check] Token OK mais utilisateur inexistant ou inactif: {}", email);
                            }
                        }
                    } catch (Exception e) {
                        logger.error("[/auth/check] Erreur de validation du token: {}", e.getMessage(), e);
                    }
                    break;
                }
            }
        }

        if (!isAuthenticated) {
            logger.warn("Cookie 'token' non trouvé ou invalide");
        }

        return ResponseEntity.ok(isAuthenticated);
    }

    @GetMapping("/me")
    @Operation(
        summary = "Récupérer l'utilisateur courant",
        description = "Récupère les informations de l'utilisateur actuellement authentifié."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Informations utilisateur récupérées",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    public ResponseEntity<UserDto> getCurrentUser(
        @Parameter(hidden = true) Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(
            UserMapper.convertToAuthDto(user));
    }

    @PostMapping("/login")
    @Operation(
        summary = "Se connecter",
        description = "Authentifie un utilisateur avec son email et mot de passe."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Connexion réussie"),
        @ApiResponse(responseCode = "401", description = "Identifiants invalides", content = @Content),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    public ResponseEntity<?> authenticate(
        @Parameter(description = "Données de connexion")
        @Valid @RequestBody LoginRequestDto loginRequestDto,
        @Parameter(hidden = true) HttpServletResponse response) {
        String token = authenticationService.authenticate(
            loginRequestDto.getEmail(),
            loginRequestDto.getPassword());

        jwtCookieService.addJwtCookie(response, token);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    @Operation(
        summary = "Compléter la première connexion",
        description = "Finalise l'inscription d'un utilisateur invité en définissant son mot de passe."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inscription finalisée avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides ou token expiré", content = @Content),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content)
    })
    public ResponseEntity<?> completeFirstLogin(
        @Parameter(description = "Données de finalisation d'inscription")
        @RequestBody FirstLoginDto dto) {
        userService.completeFirstLogin(dto);
        return ResponseEntity.ok("Mot de passe défini. Vous pouvez maintenant vous connecter.");
    }

    @PostMapping("/forgot-password")
    @Operation(
        summary = "Demander une réinitialisation de mot de passe",
        description = "Envoie un email de réinitialisation de mot de passe si l'email existe."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Demande traitée (email envoyé si l'adresse existe)"),
        @ApiResponse(responseCode = "400", description = "Format d'email invalide", content = @Content)
    })
    public ResponseEntity<?> forgotPassword(
        @Parameter(description = "Email pour la réinitialisation")
        @Valid @RequestBody ForgotPasswordRequestDto dto) {
        authenticationService.handleForgotPassword(dto.getEmail());
        return ResponseEntity.ok("Si cet email existe, un lien de réinitialisation a été envoyé.");
    }

    @PostMapping("/reset-password")
    @Operation(
        summary = "Réinitialiser le mot de passe",
        description = "Réinitialise le mot de passe avec un token de réinitialisation valide."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mot de passe réinitialisé avec succès"),
        @ApiResponse(responseCode = "400", description = "Token invalide ou expiré", content = @Content),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content)
    })
    public ResponseEntity<?> resetPassword(
        @Parameter(description = "Token et nouveau mot de passe")
        @Valid @RequestBody ResetPasswordRequestDto request) {
        authenticationService.resetPassword(request.getResetPasswordToken(), request.getNewPassword());
        return ResponseEntity.ok("Mot de passe réinitialisé avec succès.");
    }

    @PostMapping("/logout")
    @Operation(
        summary = "Se déconnecter",
        description = "Déconnecte l'utilisateur en supprimant le cookie d'authentification."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Déconnexion réussie")
    })
    public ResponseEntity<?> logout(
        @Parameter(hidden = true) HttpServletResponse response) {
        jwtCookieService.removeJwtCookie(response);
        return ResponseEntity.ok().body(Map.of("message", "Déconnexion réussie"));
    }
}
