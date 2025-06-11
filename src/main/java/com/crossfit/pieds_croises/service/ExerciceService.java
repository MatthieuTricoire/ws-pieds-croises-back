package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.dto.ExerciceDTO;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.mapper.ExerciceMapper;
import com.crossfit.pieds_croises.model.Exercice;
import com.crossfit.pieds_croises.repository.ExerciceRepository;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Getter
@Setter
@Builder
@AllArgsConstructor
public class ExerciceService {

    private final ExerciceRepository exerciceRepository;
    private final ExerciceMapper exerciceMapper;

    public List<ExerciceDTO> getAllExercices() {
        List<Exercice> exercices = exerciceRepository.findAll();
        return exercices.stream().map(exerciceMapper::convertToDTO).toList();
    }

    public ExerciceDTO getExerciceById(long id) {
        Exercice exercice = exerciceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercice with id " + id + " not found!"));
        return exerciceMapper.convertToDTO(exercice);
    }

    public ExerciceDTO createExercice(Exercice exercice) {
        Exercice savedExercice = exerciceRepository.save(exercice);
        return exerciceMapper.convertToDTO(savedExercice);
    }

    public ExerciceDTO updateExercice(long id, Exercice exerciceDetails) {
        Exercice exercice = exerciceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercice with id " + id + " not found!"));
        exercice.setName(exerciceDetails.getName());
        exercice.setMeasureType(exerciceDetails.getMeasureType());
        exercice.setPerformanceHistoryList(exerciceDetails.getPerformanceHistoryList());
        Exercice updatedExercice = exerciceRepository.save(exercice);
        return exerciceMapper.convertToDTO(updatedExercice);
    }

    public void deleteExercice(long id) {
        Exercice exercice = exerciceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercice with id " + id + " not found!"));
        exerciceRepository.delete(exercice);
    }
}
