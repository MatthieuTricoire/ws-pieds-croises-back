package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.CourseCreateDTO;
import com.crossfit.pieds_croises.dto.CourseDTO;
import com.crossfit.pieds_croises.dto.CourseUpdateDTO;
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
import org.springframework.web.bind.annotation.*;

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

}
