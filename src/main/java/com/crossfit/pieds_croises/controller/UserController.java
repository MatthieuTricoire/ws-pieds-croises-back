package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.CourseDTO;
import com.crossfit.pieds_croises.dto.UserDto;
import com.crossfit.pieds_croises.dto.UserUpdateDto;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.model.UserCourse;
import com.crossfit.pieds_croises.repository.UserRepository;
import com.crossfit.pieds_croises.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "Gestion des utilisateurs")
public class UserController {

    private static final long MAX_FILE_SIZE = 2_000_000L; // 2 Mo
    private final UserRepository userRepository;
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(
        summary = "Récupérer tous les utilisateurs",
        description = "Récupère la liste de tous les utilisateurs. Réservé aux administrateurs."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content),
        @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
    })
    public ResponseEntity<List<UserDto>> getAllUsers(
        @Parameter(description = "Inclure les abonnements dans la réponse", example = "false")
        @RequestParam(defaultValue = "false") boolean includeSubscriptions
    ) {
        List<UserDto> userDtos = userService.getAllUsers(includeSubscriptions);
        return ResponseEntity.ok(userDtos);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    @Operation(
        summary = "Récupérer un utilisateur par ID",
        description = "Récupère les détails d'un utilisateur spécifique. Réservé aux administrateurs."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Utilisateur trouvé",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content),
        @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content)
    })
    public ResponseEntity<UserDto> getUserById(
        @Parameter(description = "ID de l'utilisateur", example = "1")
        @PathVariable Long id) {
        UserDto userDto = userService.getUserById(id);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/profile")
    @Operation(
        summary = "Récupérer mon profil",
        description = "Récupère le profil de l'utilisateur connecté."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profil récupéré avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content)
    })
    public ResponseEntity<UserDto> getMyProfile(
        @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        UserDto userDto = userService.getMyProfile(user.getId());
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/courses")
    @Operation(
        summary = "Récupérer mes cours",
        description = "Récupère la liste des cours de l'utilisateur connecté."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cours récupérés avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content)
    })
    public ResponseEntity<List<CourseDTO>> getUserCourses(
                @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @RequestParam(value = "status", required = false) UserCourse.Status status) {

        List<CourseDTO> myCourses = userService.getUserCourses(user.getId(), status);
        return ResponseEntity.ok(myCourses);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(
        summary = "Créer un utilisateur",
        description = "Crée un nouvel utilisateur. Réservé aux administrateurs."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content),
        @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
    })
    public ResponseEntity<UserDto> createUser(
        @Parameter(description = "Données de l'utilisateur à créer")
        @Valid @RequestBody UserDto user) {
        UserDto invitedUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(invitedUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(
        summary = "Mettre à jour un utilisateur",
        description = "Met à jour les informations d'un utilisateur. Réservé aux administrateurs."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content),
        @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content)
    })
    public ResponseEntity<UserDto> updateUser(
        @Parameter(description = "ID de l'utilisateur", example = "1")
        @PathVariable Long id,
        @Parameter(description = "Nouvelles données de l'utilisateur")
        @Valid @RequestBody UserUpdateDto userDetails) {
        UserDto userDto = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/profile")
    @Operation(
        summary = "Mettre à jour mon profil",
        description = "Met à jour le profil de l'utilisateur connecté."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profil mis à jour avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content)
    })
    public ResponseEntity<UserDto> updateProfile(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @Parameter(description = "Nouvelles données du profil")
        @Valid @RequestBody UserUpdateDto userDetails) {
        String username = user.getEmail();
        UserDto userDto = userService.updateProfile(username, userDetails);
        return ResponseEntity.ok(userDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Supprimer un utilisateur",
        description = "Supprime un utilisateur. Réservé aux administrateurs."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Utilisateur supprimé avec succès"),
        @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content),
        @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content)
    })
    public ResponseEntity<Void> deleteUser(
        @Parameter(description = "ID de l'utilisateur", example = "1")
        @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/profile")
    @Operation(
        summary = "Supprimer mon profil",
        description = "Supprime le profil de l'utilisateur connecté."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Profil supprimé avec succès"),
        @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content)
    })
    public ResponseEntity<Void> deleteUserProfile(
        @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        userService.deleteUser(user.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/profile/profile-picture")
    @Operation(
        summary = "Uploader une photo de profil",
        description = "Upload une photo de profil pour l'utilisateur connecté."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Photo uploadée avec succès"),
        @ApiResponse(responseCode = "400", description = "Fichier invalide", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content),
        @ApiResponse(responseCode = "500", description = "Erreur serveur", content = @Content)
    })
    public ResponseEntity<String> uploadProfilePicture(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "Fichier image à uploader")
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

        // 🔹 Supprimer l'ancienne photo si elle existe
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

    @DeleteMapping("/profile/profile-picture")
    @Operation(
        summary = "Supprimer ma photo de profil",
        description = "Supprime la photo de profil de l'utilisateur connecté."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Photo supprimée avec succès"),
        @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content),
        @ApiResponse(responseCode = "500", description = "Erreur serveur", content = @Content)
    })
    public ResponseEntity<String> deleteProfilePicture(
        @Parameter(hidden = true) @AuthenticationPrincipal User user) {
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