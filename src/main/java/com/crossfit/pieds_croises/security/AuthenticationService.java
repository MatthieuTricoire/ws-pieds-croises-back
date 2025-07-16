package com.crossfit.pieds_croises.security;

import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.repository.UserRepository;
import com.crossfit.pieds_croises.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.base-url}${app.reset-password.uri}")
    private String resetPasswordUrl;
    @Value("${app.reset-password.token-expiration-minutes}")
    private int resetPasswordTokenExpiryMinutes;

    public String authenticate(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        return jwtService.generateToken((UserDetails) authentication.getPrincipal());
    }

    public void handleForgotPassword(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        logger.info("Requesting new password for user with email: {}", email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String token = generateResetToken(user);
            String resetPasswordEmailLink = emailService.generateInvitationLink(resetPasswordUrl, token);
            Map<String, Object> variables = Map.of(
                    "resetPasswordEmailLink", resetPasswordEmailLink
            );
            emailService.sendTemplateEmail(user.getEmail(), "Réinitialisation de votre mot de passe",
                    "reset-password",
                    variables);
        }
    }

    public String generateResetToken(User user) {
        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiryDate(LocalDateTime.now().plusMinutes(resetPasswordTokenExpiryMinutes));
        userRepository.save(user);
        return token;
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalide"));

        if (user.getResetPasswordTokenExpiryDate() == null ||
                user.getResetPasswordTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Le lien de réinitialisation a expiré. Veuillez faire une nouvelle demande.");
        }

        // Encoder le nouveau mot de passe
        user.setPassword(passwordEncoder.encode(newPassword));

        // Supprimer le token de reset
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiryDate(null);

        userRepository.save(user);
    }
}
