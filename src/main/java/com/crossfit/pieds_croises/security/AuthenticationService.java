package com.crossfit.pieds_croises.security;

import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.repository.UserRepository;
import com.crossfit.pieds_croises.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.base-url}")
    private String baseUrl;

    public String authenticate(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        return jwtService.generateToken((UserDetails) authentication.getPrincipal());
    }

    public void handleForgotPassword(String email) {
        // 1. Vérifie si l'utilisateur existe
        // 2. Génère un token de réinitialisation (UUID)
        // 3. Envoie un e-mail avec un lien vers une page front-end
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String token = generateResetToken(user);
            String resetUrl = baseUrl + "reset-password?token=" + token;
            emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);
        }
        // Sinon, on ne dit rien pour éviter de révéler si l’email existe ou pas
    }

    public String generateResetToken(User user) {
        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiryDate(LocalDateTime.now().plusMinutes(30));
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
