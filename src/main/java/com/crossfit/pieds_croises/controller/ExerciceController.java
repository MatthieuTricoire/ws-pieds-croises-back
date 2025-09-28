package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.ExerciceDTO;
import com.crossfit.pieds_croises.model.Exercice;
import com.crossfit.pieds_croises.service.ExerciceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exercices")
@RequiredArgsConstructor
@Tag(name = "Exercice", description = "Gestion des exercices")
public class ExerciceController {

    private final ExerciceService exerciceService;

    @GetMapping
    public ResponseEntity<List<ExerciceDTO>> getAllExercices() {
        List<ExerciceDTO> exercices = exerciceService.getAllExercices();
        if (exercices.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(exercices);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciceDTO> getExerciceById(@PathVariable Long id) {
        ExerciceDTO exercice = exerciceService.getExerciceById(id);
        return ResponseEntity.ok(exercice);
    }

    @PostMapping
    public ResponseEntity<ExerciceDTO> createExercice(@RequestBody Exercice exercice) {
        ExerciceDTO savedExercice = exerciceService.createExercice(exercice);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedExercice);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExerciceDTO> updateExercice(@PathVariable Long id, @RequestBody Exercice exerciceDetails) {
        ExerciceDTO updatedExercice = exerciceService.updateExercice(id, exerciceDetails);
        return ResponseEntity.ok(updatedExercice);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExercice(@PathVariable Long id) {
        exerciceService.deleteExercice(id);
        return ResponseEntity.noContent().build();
    }
}
