package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.ExerciceDTO;
import com.crossfit.pieds_croises.model.Exercice;
import com.crossfit.pieds_croises.service.ExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping
    public ResponseEntity<List<ExerciceDTO>> getAllExercises() {
        List<ExerciceDTO> exercises = exerciseService.getAllExercises();
        if (exercises.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciceDTO> getExerciseById(@PathVariable Long id) {
        ExerciceDTO exercise = exerciseService.getExerciseById(id);
        return ResponseEntity.ok(exercise);
    }

    @PostMapping
    public ResponseEntity<ExerciceDTO> createExercise(@RequestBody Exercice exercise) {
        ExerciceDTO savedExercise = exerciseService.createExercise(exercise);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedExercise);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExerciceDTO> updateExercise(@PathVariable Long id, @RequestBody Exercice exerciseDetails) {
        ExerciceDTO updatedExercise = exerciseService.updateExercise(id, exerciseDetails);
        return ResponseEntity.ok(updatedExercise);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable Long id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.noContent().build();
    }
}
