package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.CourseDTO;
import com.crossfit.pieds_croises.dto.UserDto;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.service.UserCourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/userCourses")
public class UserCourseController {

    private final UserCourseService userCourseService;

    @PutMapping("/{courseId}/register")
    @Operation(
            summary = "S'inscrire à un cours",
            description = "Inscrit l'utilisateur connecté à un cours spécifique."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscription réussie",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Inscription impossible", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cours non trouvé", content = @Content)
    })
    public ResponseEntity<CourseDTO> registerToCourse(@PathVariable Long courseId, @AuthenticationPrincipal User user) {
        CourseDTO courseWithNewUser = userCourseService.addUserToCourse(courseId, user.getId());
        return ResponseEntity.ok(courseWithNewUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{courseId}/users/{userId}")
    @Operation(
            summary = "Ajouter un utilisateur à un cours",
            description = "Ajoute un utilisateur spécifique à un cours. Réservé aux administrateurs."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur ajouté avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Ajout impossible", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cours ou utilisateur non trouvé", content = @Content)
    })
    public ResponseEntity<CourseDTO> addUserToCourse(
            @Parameter(description = "ID du cours", example = "1")
            @PathVariable Long courseId,
            @Parameter(description = "ID de l'utilisateur", example = "1")
            @PathVariable Long userId) {
        CourseDTO course = userCourseService.addUserToCourse(courseId, userId);
        return ResponseEntity.ok(course);
    }

    @DeleteMapping("/{courseId}/unsubscribe")
    @Operation(
            summary = "Se désinscrire d'un cours",
            description = "Désinscrit l'utilisateur connecté d'un cours spécifique."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Désinscription réussie",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Désinscription impossible", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cours non trouvé", content = @Content)
    })
    public ResponseEntity<CourseDTO> unsubscribeFromCourse(@PathVariable Long courseId,
                                                           @AuthenticationPrincipal User user) {
        CourseDTO courseMinusOneUser = userCourseService.deleteUserFromCourse(courseId, user.getId());
        return ResponseEntity.ok(courseMinusOneUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{courseId}/users/{userId}")
    @Operation(
            summary = "Retirer un utilisateur d'un cours",
            description = "Retire un utilisateur spécifique d'un cours. Réservé aux administrateurs."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur retiré avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Retrait impossible", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cours ou utilisateur non trouvé", content = @Content)
    })
    public ResponseEntity<CourseDTO> removeUserFromCourse(
            @Parameter(description = "ID du cours", example = "1")
            @PathVariable Long courseId,
            @Parameter(description = "ID de l'utilisateur", example = "1")
            @PathVariable Long userId) {
        CourseDTO course = userCourseService.deleteUserFromCourse(courseId, userId);
        return ResponseEntity.ok(course);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{courseId}/available-users")
    @Operation(
            summary = "Récupérer les utilisateurs disponibles",
            description = "Récupère la liste des utilisateurs qui ne sont pas encore inscrits au cours. Réservé aux administrateurs."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cours non trouvé", content = @Content)
    })
    public ResponseEntity<List<UserDto>> getAvailableUsers(@PathVariable Long courseId) {
        List<UserDto> users = userCourseService.getUsersNotInCourse(courseId);
        return ResponseEntity.ok(users);
    }


    @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
    @GetMapping("/user/{userId}/weekly-count")
    @Operation(
            summary = "Récupérer le nombre de cours hebdomadaires",
            description = "Récupère le nombre de cours suivis par un utilisateur pour une semaine donnée."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nombre récupéré avec succès"),
            @ApiResponse(responseCode = "400", description = "Format de date invalide", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content)
    })
    public ResponseEntity<Long> getUserWeeklyCourseCount(@PathVariable Long userId,
                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekDate) {
        Long count = userCourseService.getUserWeeklyCourseCount(userId, weekDate);
        return ResponseEntity.ok(count);
    }
}
