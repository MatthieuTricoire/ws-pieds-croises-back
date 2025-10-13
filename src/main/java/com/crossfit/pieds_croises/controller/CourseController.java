package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.CourseCreateDTO;
import com.crossfit.pieds_croises.dto.CourseDTO;
import com.crossfit.pieds_croises.dto.CourseUpdateDTO;
import com.crossfit.pieds_croises.dto.UserDto;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/courses")
@Tag(name = "Course", description = "Gestion des cours")
public class CourseController {

  private final CourseService courseService;

  @GetMapping
  @Operation(
      summary = "Récupérer tous les cours",
      description = "Récupère la liste complète de tous les cours disponibles."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseDTO.class)))
  })
  public ResponseEntity<List<CourseDTO>> getAllCourses() {
    List<CourseDTO> courses = courseService.getAllCourses();
    return ResponseEntity.ok(courses);
  }

  @GetMapping("/next-two-weeks")
  @Operation(
      summary = "Récupérer les cours des deux prochaines semaines",
      description = "Récupère la liste des cours programmés pour les deux prochaines semaines."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseDTO.class)))
  })
  public ResponseEntity<List<CourseDTO>> getCoursesNextTwoWeeks() {
    List<CourseDTO> courses = courseService.getCoursesNextTwoWeeks();
    return ResponseEntity.ok(courses);
  }

  @GetMapping("/by-day")
  @Operation(
      summary = "Récupérer les cours par jour",
      description = "Récupère la liste des cours programmés pour une date spécifique."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseDTO.class))),
      @ApiResponse(responseCode = "400", description = "Format de date invalide", content = @Content)
  })
  public ResponseEntity<List<CourseDTO>> getCoursesByDay(
      @Parameter(description = "Date au format YYYY-MM-DD", example = "2024-01-15")
      @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    List<CourseDTO> courses = courseService.getCoursesByDay(date);
    return ResponseEntity.ok(courses);
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Récupérer un cours par ID",
      description = "Récupère les détails d'un cours spécifique."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Cours trouvé",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseDTO.class))),
      @ApiResponse(responseCode = "404", description = "Cours non trouvé", content = @Content)
  })
  public ResponseEntity<CourseDTO> getCourseById(
      @Parameter(description = "ID du cours", example = "1")
      @PathVariable Long id) {
    CourseDTO courseDTO = courseService.getCourseByID(id);
    return ResponseEntity.ok(courseDTO);
  }

  @PostMapping
  @Operation(
      summary = "Créer un cours",
      description = "Crée un nouveau cours."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Cours créé avec succès",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseDTO.class))),
      @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
      @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content)
  })
  public ResponseEntity<CourseDTO> createCourse(
      @Parameter(description = "Données du cours à créer")
      @Valid @RequestBody CourseCreateDTO courseCreateDTO) {
    CourseDTO savedCourse = courseService.createCourse(courseCreateDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedCourse);
  }

  @PutMapping("/{id}")
  @Operation(
      summary = "Mettre à jour un cours",
      description = "Met à jour les informations d'un cours existant."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Cours mis à jour avec succès",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseDTO.class))),
      @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
      @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content),
      @ApiResponse(responseCode = "404", description = "Cours non trouvé", content = @Content)
  })
  public ResponseEntity<CourseDTO> updateCourse(
      @Parameter(description = "ID du cours", example = "1")
      @PathVariable Long id,
      @Parameter(description = "Nouvelles données du cours")
      @Valid @RequestBody CourseUpdateDTO courseUpdateDTO) {
    CourseDTO updateCourse = courseService.updateCourse(id, courseUpdateDTO);
    return ResponseEntity.ok(updateCourse);
  }

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
  public ResponseEntity<CourseDTO> registerToCourse(
      @Parameter(description = "ID du cours", example = "1")
      @PathVariable Long courseId, 
      @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    CourseDTO courseWithNewUser = courseService.addUserToCourse(courseId, user.getId());
    return ResponseEntity.ok(courseWithNewUser);
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
  public ResponseEntity<CourseDTO> unsubscribeFromCourse(
      @Parameter(description = "ID du cours", example = "1")
      @PathVariable Long courseId,
      @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    CourseDTO courseMinusOneUser = courseService.deleteUserFromCourse(courseId, user.getId());
    return ResponseEntity.ok(courseMinusOneUser);
  }

  @DeleteMapping("/{id}")
  @Operation(
      summary = "Supprimer un cours",
      description = "Supprime un cours existant."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Cours supprimé avec succès"),
      @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content),
      @ApiResponse(responseCode = "404", description = "Cours non trouvé", content = @Content)
  })
  public ResponseEntity<Void> deleteCourse(
      @Parameter(description = "ID du cours", example = "1")
      @PathVariable Long id) {
    courseService.deleteCourse(id);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{id}/available-users")
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
  public ResponseEntity<List<UserDto>> getAvailableUsers(
      @Parameter(description = "ID du cours", example = "1")
      @PathVariable Long id) {
    List<UserDto> users = courseService.getUsersNotInCourse(id);
    return ResponseEntity.ok(users);
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
    CourseDTO course = courseService.addUserToCourse(courseId, userId);
    return ResponseEntity.ok(course);
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
    CourseDTO course = courseService.deleteUserFromCourse(courseId, userId);
    return ResponseEntity.ok(course);
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
  public ResponseEntity<Long> getUserWeeklyCourseCount(
      @Parameter(description = "ID de l'utilisateur", example = "1")
      @PathVariable Long userId,
      @Parameter(description = "Date de la semaine au format YYYY-MM-DD", example = "2024-01-15")
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekDate) {
    Long count = courseService.getUserWeeklyCourseCount(userId, weekDate);
    return ResponseEntity.ok(count);
  }

}
