package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.dto.*;
import com.crossfit.pieds_croises.exception.DuplicateResourceException;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.mapper.CourseMapper;
import com.crossfit.pieds_croises.mapper.UserMapper;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.model.UserCourse;
import com.crossfit.pieds_croises.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    private final CourseMapper courseMapper;
    private final UserSubscriptionService userSubscriptionService;

    @Value("${app.base-url}${app.registration.uri}")
    private String registrationUrl;
    @Value("${app.registration.token-expiration-days}")
    private int registrationTokenExpirationDays;

    public List<UserDto> getAllUsers(boolean includeSubscriptions) {
        List<User> users;
        if (includeSubscriptions) {
            users = userRepository.findAllWithUserSubscriptions();
        } else {
            users = userRepository.findAll();
        }
        if (users.isEmpty()) {
            logger.warn("No users found in the database");
            throw new ResourceNotFoundException("No users found");
        }
        logger.info("Found {} users", users.size());
        return users.stream()
                .map(user -> includeSubscriptions ? userMapper.convertToDtoForAdminWithSubscriptions(user)
                        : userMapper.convertToDtoForAdmin(user))
                .toList();
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.convertToDtoForAdmin(user);
    }

    public UserDto getMyProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.convertToDtoForUser(user);
    }

    public List<CourseDTO> getUserCourses(Long id, UserCourse.Status status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        LocalDateTime now = LocalDateTime.now();

        return user.getUserCourses().stream()
                .filter(uc -> status == null || uc.getStatus() == status) // si status est null, on ne filtre pas
                .map(UserCourse::getCourse)
                .filter(course -> course.getStartDatetime().isAfter(now)) // uniquement les cours à venir
                .map(courseMapper::convertToDto)
                .toList();
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
        Set<String> roles = userDto.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = Set.of("ROLE_USER"); // Valeur par défaut si aucun rôle fourni
        }
        user.setRoles(roles);

        String token = UUID.randomUUID().toString();
        user.setRegistrationToken(token);
        user.setRegistrationTokenExpiryDate(LocalDateTime.now().plusDays(registrationTokenExpirationDays));
        user.setIsFirstLoginComplete(false);
        // Envoi du lien par email
        logger.info("Sending registration email to {}", user.getEmail());
        String registrationEmailLink = emailService.generateInvitationLink(registrationUrl, token, user.getFirstname());
        Map<String, Object> emailVariables = Map.of(
                "registrationEmailLink", registrationEmailLink);
        emailService.sendTemplateEmail(user.getEmail(),
                "Votre accès à la plateforme CrossFit Pieds Croisés",
                "first-connection",
                emailVariables);

        User createdUser = userRepository.save(user);
        logger.info("User created with ID {}", createdUser.getId());

        if (userDto.getSubscriptionId() != null) {
            try {
                UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto(
                        null,
                        null,
                        null,
                        0,
                        null,
                        createdUser.getId(),
                        userDto.getSubscriptionId(),
                        null);

                userSubscriptionService.createUserSubscription(userSubscriptionDto);
            } catch (Exception e) {
                logger.error("Failed to create subscription for user ID {}: {}", createdUser.getId(), e.getMessage());
                throw new RuntimeException("Failed to create subscription for user ID: " + createdUser.getId(), e);
            }
        }

        return userMapper.convertToCreatedDto(createdUser);
    }

    public UserDto updateUser(Long id, UserUpdateDto userDto) {
        if (userDto.getId() != null && !userDto.getId().equals(id)) {
            throw new IllegalArgumentException("ID mismatch between path variable and request body");
        }

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        userMapper.updateUserFromDto(userDto, existingUser);
        existingUser.setUpdatedAt(LocalDateTime.now());
        try {
            User updatedUser = userRepository.save(existingUser);
            return userMapper.convertToDtoForAdmin(updatedUser);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user with id: " + id, e);
        }
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

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    public void completeFirstLogin(FirstLoginDto dto) {
        logger.info("Completing first login for token {}", dto.getRegistrationToken());
        User user = userRepository.findByRegistrationToken(dto.getRegistrationToken())
                .orElseThrow(() -> {
                    logger.warn("Invalid registration token :{}", dto.getRegistrationToken());
                    return new ResourceNotFoundException("Invalid registration token");
                });

        if (user.getRegistrationTokenExpiryDate().isBefore(LocalDateTime.now())) {
            logger.warn("Registration token expired for user ID: {}", user.getId());
            throw new RuntimeException("Lien expiré");
        }

        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRegistrationToken(null);
        user.setRegistrationTokenExpiryDate(null);
        user.setIsFirstLoginComplete(true);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        logger.info("First long completed for user ID {}", user.getId());
    }
}
