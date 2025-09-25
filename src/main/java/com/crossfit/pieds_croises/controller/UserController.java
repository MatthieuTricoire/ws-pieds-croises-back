package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.CourseDTO;
import com.crossfit.pieds_croises.dto.UserDto;
import com.crossfit.pieds_croises.dto.UserUpdateDto;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.model.UserCourse;
import com.crossfit.pieds_croises.repository.UserRepository;
import com.crossfit.pieds_croises.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private static final long MAX_FILE_SIZE = 2_000_000L; // 2 Mo
    private final UserRepository userRepository;
    private UserService userService;

    // READ ALL
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers(
            @RequestParam(defaultValue = "false") boolean includeSubscriptions
    ) {
        List<UserDto> userDtos = userService.getAllUsers(includeSubscriptions);
        return ResponseEntity.ok(userDtos);
    }

    // READ ONE
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto userDto = userService.getUserById(id);
        return ResponseEntity.ok(userDto);
    }

    // READ USER PROFILE
    @GetMapping("/profile")
    public ResponseEntity<UserDto> getMyProfile(@AuthenticationPrincipal User user) {
        UserDto userDto = userService.getMyProfile(user.getId());
        return ResponseEntity.ok(userDto);
    }

    // READ USER COURSES
    @GetMapping("/courses")
    public ResponseEntity<?> getUserCourses(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "status", required = false) String status) {

        UserCourse.Status enumStatus = null;
        if (status != null) {
            try {
                enumStatus = UserCourse.Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(
                        Map.of(
                                "error", "Invalid status value",
                                "message", "Status must be one of: REGISTERED, WAITING_LIST, CANCELLED"
                        )
                );
            }
        }
        List<CourseDTO> myCourses = userService.getUserCourses(user.getId(), enumStatus);
        return ResponseEntity.ok(myCourses);
    }

    // CREATE
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto user) {
        UserDto invitedUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(invitedUser);
    }

    // UPDATE
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDto userDetails) {
        UserDto userDto = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(userDto);
    }

    // UPDATE USER PROFILE
    @PutMapping("/profile")
    public ResponseEntity<UserDto> updateProfile(@AuthenticationPrincipal User user,
                                                 @Valid @RequestBody UserUpdateDto userDetails) {
        String username = user.getEmail();
        UserDto userDto = userService.updateProfile(username, userDetails);
        return ResponseEntity.ok(userDto);
    }

    // DELETE
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // DELETE USER PROFILE
    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteUserProfile(@AuthenticationPrincipal User user) {
        userService.deleteUser(user.getId());
        return ResponseEntity.noContent().build();
    }

    // UPLOAD USER PROFILE PICTURE
    @PostMapping("/profile/profile-picture")
    public ResponseEntity<String> uploadProfilePicture(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file) throws IOException {

        // Créer le dossier si nécessaire
        Path uploadDir = Paths.get("uploads/profile-pictures");
        Files.createDirectories(uploadDir);

        // Vérifier l’extension et la taille
        String contentType = file.getContentType();
        if (!List.of("image/png", "image/jpeg", "image/jpg").contains(contentType)) {
            return ResponseEntity.badRequest().body("Format de fichier non supporté");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            return ResponseEntity.badRequest().body("Fichier trop volumineux");
        }

        // Supprimer l'ancienne photo si elle existe
        if (user.getProfilePicture() != null) {
            Path oldFile = Paths.get(user.getProfilePicture().replaceFirst("^/", "")); // enlever le "/" initial
            try {
                if (Files.exists(oldFile)) {
                    Files.delete(oldFile);
                }
            } catch (IOException ex) {
                // Log et continuer (ne pas bloquer l’upload)
                System.err.println("Impossible de supprimer l'ancienne photo : " + oldFile + " -> " + ex.getMessage());
            }
        }

        // Générer un nom unique
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + "-" + user.getFirstname() + "-" + user.getLastname() + "." + extension;
        Path filepath = uploadDir.resolve(filename);

        // Sauvegarder le nouveau fichier
        Files.write(filepath, file.getBytes());

        // Mettre à jour l’utilisateur
        user.setProfilePicture("/uploads/profile-pictures/" + filename);
        userRepository.save(user);

        return ResponseEntity.ok(user.getProfilePicture());
    }

    // DELETE USER PROFILE PICTURE
    @DeleteMapping("/profile/profile-picture")
    public ResponseEntity<String> deleteProfilePicture(@AuthenticationPrincipal User user) {
        try {
            // Vérifie qu'une photo existe
            if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {

                // Récupère uniquement le nom du fichier
                String filename = Paths.get(user.getProfilePicture()).getFileName().toString();

                // Construit le chemin réel côté serveur
                Path filePath = Paths.get("uploads/profile-pictures").resolve(filename).toAbsolutePath();

                // Supprime le fichier s’il existe
                Files.deleteIfExists(filePath);

                // Supprime la référence dans la base
                user.setProfilePicture(null);
                userRepository.save(user);
            }

            return ResponseEntity.ok("Photo de profil supprimée");

        } catch (IOException e) {
            // Gère les erreurs et retourne un message clair
            return ResponseEntity.status(500).body("Impossible de supprimer la photo de profil : " + e.getMessage());
        }
    }

}