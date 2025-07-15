package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.dto.FirstLoginDto;
import com.crossfit.pieds_croises.dto.UserDto;
import com.crossfit.pieds_croises.dto.UserUpdateDto;
import com.crossfit.pieds_croises.exception.DuplicateResourceException;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.mapper.UserMapper;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    @Value("${app.registration.base-url}")
    private String registrationBaseUrl;

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            logger.warn("No users found in the database");
            throw new ResourceNotFoundException("No users found");
        }
        logger.info("Found {} users", users.size());
        return users.stream()
                .map(userMapper::convertToDtoForAdmin)
                .toList();
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.convertToDtoForAdmin(user);
    }

    public UserDto createUser(UserDto userDto) {
        logger.info("Creating user {}", userDto.getEmail());
        User existingUser = userRepository.findByEmail(userDto.getEmail())
                .orElse(null);
        if (existingUser != null) {
            logger.warn("Attempt to create user with existing email: {}", userDto.getEmail());
            throw new DuplicateResourceException("User already exists");
        }
        User user = userMapper.convertToEntity(userDto);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        //user.setRoles(Set.of("ROLE_USER")); // role user par défaut
        Set<String> roles = userDto.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = Set.of("ROLE_USER"); // Valeur par défaut si aucun rôle fourni
        }
        user.setRoles(roles);

        // génération du token pour la première connexion
        String token = UUID.randomUUID().toString();
        user.setRegistrationToken(token);
        user.setTokenExpiryDate(LocalDateTime.now().plusDays(2));
        user.setIsFirstLoginComplete(false);
        // Envoi du lien par email
        logger.info("Sending registration email to {}", user.getEmail());
        String registrationEmailLink = emailService.generateInvitationLink(registrationBaseUrl, token);
        emailService.sendInvitationEmail(user.getEmail(), registrationEmailLink);

        User createdUser = userRepository.save(user);
        logger.info("User created with ID {}", createdUser.getId());

        return userMapper.convertToCreatedDto(createdUser);

    }

    public UserDto updateUser(Long id, UserUpdateDto userDto) {
        logger.info("Updating user with ID: {}", userDto.getId());
        if (userDto.getId() != null && !userDto.getId().equals(id)) {
            logger.error("ID mismatch : path ID {} does not match body ID {}", id, userDto.getId());
            throw new IllegalArgumentException("ID mismatch between path variable and request body");
        }

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        userMapper.updateUserFromDto(userDto, existingUser);
        existingUser.setUpdatedAt(LocalDateTime.now());

        try {
            User updatedUser = userRepository.save(existingUser);
            logger.info("User updated with ID {}", updatedUser.getId());
            return userMapper.convertToDtoForAdmin(updatedUser);
        } catch (Exception e) {
            logger.error("Failed to update user with ID: {}", id, e);
            throw new RuntimeException("Failed to update user with id: " + id, e);
        }
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    public UserDto getMyProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.convertToDtoForUser(user);
    }

    public UserDto updateProfile(String username, UserUpdateDto userDto) {
        User existingUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username));

        userMapper.updateUserFromDto(userDto, existingUser);
        existingUser.setUpdatedAt(LocalDateTime.now());

        try {
            User updatedUser = userRepository.save(existingUser);
            return userMapper.convertToDtoForUser(updatedUser);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user: " + username, e);
        }

    }

    public void completeFirstLogin(FirstLoginDto dto) {
        logger.info("Completing first login for token {}", dto.getRegistrationToken());
        User user = userRepository.findByRegistrationToken(dto.getRegistrationToken())
                .orElseThrow(() -> {
                    logger.warn("Invalid registration token :{}", dto.getRegistrationToken());
                    return new ResourceNotFoundException("Invalid registration token");
                });

        if (user.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
            logger.warn("Registration token expired for user ID: {}", user.getId());
            throw new RuntimeException("Registration token expired");
        }

        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRegistrationToken(null); // Token à usage unique
        user.setTokenExpiryDate(null);
        user.setIsFirstLoginComplete(true);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        logger.info("First long completed for user ID {}", user.getId());
    }
}
