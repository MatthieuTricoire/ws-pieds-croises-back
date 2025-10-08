package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.dto.ExerciceDTO;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.mapper.ExerciceMapper;
import com.crossfit.pieds_croises.model.Exercice;
import com.crossfit.pieds_croises.repository.ExerciseRepository;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Getter
@Setter
@Builder
@AllArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciceMapper exerciseMapper;

    public List<ExerciceDTO> getAllExercises() {
        List<Exercice> exercises = exerciseRepository.findAll();
        return exercises.stream().map(exerciseMapper::convertToDTO).toList();
    }

    public ExerciceDTO getExerciseById(Long id) {
        Exercice exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise with id " + id + " not found!"));
        return exerciseMapper.convertToDTO(exercise);
    }

    public ExerciceDTO createExercise(Exercice exercise) {
        Exercice savedExercise = exerciseRepository.save(exercise);
        return exerciseMapper.convertToDTO(savedExercise);
    }

    public ExerciceDTO updateExercise(Long id, Exercice exerciseDetails) {
        Exercice exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise with id " + id + " not found!"));
        exercise.setName(exerciseDetails.getName());
        exercise.setMeasureType(exerciseDetails.getMeasureType());
        exercise.setPerformanceHistoryList(exerciseDetails.getPerformanceHistoryList());
        Exercice updatedExercise = exerciseRepository.save(exercise);
        return exerciseMapper.convertToDTO(updatedExercise);
    }

    public void deleteExercise(Long id) {
        Exercice exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise with id " + id + " not found!"));
        exerciseRepository.delete(exercise);
    }
}
