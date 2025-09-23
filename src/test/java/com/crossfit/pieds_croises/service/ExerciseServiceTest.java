package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.dto.ExerciceDTO;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.mapper.ExerciceMapper;
import com.crossfit.pieds_croises.model.Exercice;
import com.crossfit.pieds_croises.model.PerformanceHistory;
import com.crossfit.pieds_croises.repository.ExerciseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private ExerciceMapper exerciseMapper;

    @InjectMocks
    private ExerciseService exerciseService;

    @Test
    public void testGetAllExercises() {
        // Arrange
        Exercice exercise1 = new Exercice();
        Exercice exercise2 = new Exercice();
        ExerciceDTO exerciseDTO1 = new ExerciceDTO();
        ExerciceDTO exerciseDTO2 = new ExerciceDTO();
        List<Exercice> exercises = List.of(exercise1, exercise2);

        when(exerciseRepository.findAll()).thenReturn(exercises);
        when(exerciseMapper.convertToDTO(exercise1)).thenReturn(exerciseDTO1);
        when(exerciseMapper.convertToDTO(exercise2)).thenReturn(exerciseDTO2);

        // Act
        List<ExerciceDTO> result = exerciseService.getAllExercises();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(exerciseDTO1, exerciseDTO2);
        verify(exerciseRepository, times(1)).findAll();
        verify(exerciseMapper, times(1)).convertToDTO(exercise1);
        verify(exerciseMapper, times(1)).convertToDTO(exercise2);
    }

    @Test
    public void testGetExerciseById() {
        // Arrange
        Long id = 1L;
        Exercice exercise = new Exercice();
        exercise.setId(id);
        ExerciceDTO exerciseDTO = new ExerciceDTO();

        when(exerciseRepository.findById(id)).thenReturn(Optional.of(exercise));
        when(exerciseMapper.convertToDTO(exercise)).thenReturn(exerciseDTO);

        // Act
        ExerciceDTO result = exerciseService.getExerciseById(id);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(exerciseDTO);
        verify(exerciseRepository, times(1)).findById(id);
        verify(exerciseMapper, times(1)).convertToDTO(exercise);
    }

    @Test
    public void testGetExerciseById_WhenExerciseNotFound_ShouldThrownException() {
        // Arrange
        Long id = 1L;

        when(exerciseRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> exerciseService.getExerciseById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Exercise with id " + id + " not found!");
        verify(exerciseRepository, times(1)).findById(id);
        verifyNoInteractions(exerciseMapper);
    }

    @Test
    public void testCreateExercise() {
        // Arrange
        Exercice exercise = new Exercice();
        Exercice savedExercise = new Exercice();
        ExerciceDTO exerciseDTO = new ExerciceDTO();

        when(exerciseRepository.save(exercise)).thenReturn(savedExercise);
        when(exerciseMapper.convertToDTO(savedExercise)).thenReturn(exerciseDTO);

        // Act
        ExerciceDTO result = exerciseService.createExercise(exercise);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(exerciseDTO);
        verify(exerciseRepository, times(1)).save(exercise);
        verify(exerciseMapper, times(1)).convertToDTO(savedExercise);
    }

    @Test
    public void testUpdateExercise() {
        // Arrange
        Long id = 1L;
        Exercice exerciseDetails = new Exercice();
        exerciseDetails.setName("Updated Exercise");
        exerciseDetails.setMeasureType(Exercice.MeasureType.WEIGHT);
        exerciseDetails.setPerformanceHistoryList(List.of(new PerformanceHistory()));

        Exercice existingExercise = new Exercice();
        existingExercise.setId(id);
        existingExercise.setName("Old Exercise");
        existingExercise.setMeasureType(Exercice.MeasureType.REPETITION);
        existingExercise.setPerformanceHistoryList(List.of(new PerformanceHistory()));

        ExerciceDTO expectedExerciseDTO = new ExerciceDTO();
        expectedExerciseDTO.setId(id);
        expectedExerciseDTO.setName(exerciseDetails.getName());
        expectedExerciseDTO.setMeasureType(exerciseDetails.getMeasureType());

        when(exerciseRepository.findById(id)).thenReturn(Optional.of(existingExercise));
        when(exerciseRepository.save(existingExercise)).thenReturn(existingExercise);
        when(exerciseMapper.convertToDTO(existingExercise)).thenReturn(expectedExerciseDTO);

        // Act
        ExerciceDTO result = exerciseService.updateExercise(id, exerciseDetails);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedExerciseDTO);
        verify(exerciseRepository, times(1)).findById(id);
        verify(exerciseRepository, times(1)).save(existingExercise);
        verify(exerciseMapper, times(1)).convertToDTO(existingExercise);
    }

    @Test
    public void testUpdateExercise_WhenExerciseNotFound_ShouldThrownException() {
        // Arrange
        Long id = 1L;
        Exercice exerciseDetails = new Exercice();

        when(exerciseRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> exerciseService.updateExercise(id, exerciseDetails))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Exercise with id " + id + " not found!");
        verify(exerciseRepository, times(1)).findById(id);
        verifyNoMoreInteractions(exerciseRepository);
        verifyNoInteractions(exerciseMapper);
    }

    @Test
    public void testDeleteExercise() {
        // Arrange
        Long id = 1L;
        Exercice exercise = new Exercice();

        when(exerciseRepository.findById(id)).thenReturn(Optional.of(exercise));

        // Act
        exerciseService.deleteExercise(id);

        // Assert
        verify(exerciseRepository, times(1)).findById(id);
        verify(exerciseRepository, times(1)).delete(exercise);
    }

    @Test
    public void testDeleteExercise_WhenExerciseNotFound_ShouldThrownException() {
        // Arrange
        Long id = 1L;

        when(exerciseRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> exerciseService.deleteExercise(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Exercise with id " + id + " not found!");
        verify(exerciseRepository, times(1)).findById(id);
        verifyNoMoreInteractions(exerciseRepository);
    }
}
