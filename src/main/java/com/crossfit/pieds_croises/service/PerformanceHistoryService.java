package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.datetime.DateTimeProvider;
import com.crossfit.pieds_croises.dto.PerformanceHistoryDTO;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.mapper.PerformanceHistoryMapper;
import com.crossfit.pieds_croises.model.Exercice;
import com.crossfit.pieds_croises.model.PerformanceHistory;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.repository.ExerciseRepository;
import com.crossfit.pieds_croises.repository.PerformanceHistoryRepository;
import com.crossfit.pieds_croises.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PerformanceHistoryService {

    private final PerformanceHistoryRepository performanceHistoryRepository;
    private final PerformanceHistoryMapper performanceHistoryMapper;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final DateTimeProvider dateTimeProvider;

    public List<PerformanceHistoryDTO> getAllPerformanceHistory(Long userId) {
        List<PerformanceHistory> performanceHistoryList = performanceHistoryRepository.findAllByUserIdOrderByDateAsc(userId);
        return performanceHistoryList.stream()
                .map(performanceHistoryMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PerformanceHistoryDTO> getAllPerformanceHistoryForLastXMonths(Long userId, Integer months) {
        LocalDate startDate = dateTimeProvider.today().withDayOfMonth(1).minusMonths(months - 1);
        List<PerformanceHistory> performanceHistoryList = performanceHistoryRepository.findAllByUserIdForLastXMonths(userId, startDate);
        return performanceHistoryList.stream()
                .map(performanceHistoryMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    public PerformanceHistoryDTO createPerformanceHistory(PerformanceHistoryDTO performanceHistoryDTO, Long userId) {
        PerformanceHistory performanceHistory = performanceHistoryMapper.convertToEntity(performanceHistoryDTO);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found."));
        performanceHistory.setUser(user);

        Exercice exercice = exerciseRepository.findById(performanceHistoryDTO.getExerciseId())
                .orElseThrow(() -> new ResourceNotFoundException("Exercice with id " + performanceHistoryDTO.getExerciseId() + " not found."));
        performanceHistory.setExercice(exercice);

        PerformanceHistory savedPerformanceHistory = performanceHistoryRepository.save(performanceHistory);
        return performanceHistoryMapper.convertToDTO(savedPerformanceHistory);
    }

    public PerformanceHistoryDTO updatePerformanceHistory(Long id, PerformanceHistoryDTO performanceHistoryDTO, Long userId) {
        PerformanceHistory performanceHistory = performanceHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PerformanceHistory with id " + id + " not found."));

        if (!performanceHistory.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Not authorized to update this performanceHistory.");
        }

        Exercice exercice = exerciseRepository.findById(performanceHistoryDTO.getExerciseId())
                .orElseThrow(() -> new ResourceNotFoundException("Exercice with id " + performanceHistoryDTO.getExerciseId() + " not found."));

        performanceHistoryMapper.updateEntityFromDTO(performanceHistoryDTO, performanceHistory);
        performanceHistory.setExercice(exercice);
        PerformanceHistory updatedPerformanceHistory = performanceHistoryRepository.save(performanceHistory);
        return performanceHistoryMapper.convertToDTO(updatedPerformanceHistory);
    }

    public void deletePerformanceHistory(Long id, Long userId) {
        PerformanceHistory performanceHistory = performanceHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PerformanceHistory with id " + id + " not found."));
        if (!performanceHistory.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Not authorized to delete this performanceHistory.");
        }
        performanceHistoryRepository.delete(performanceHistory);
    }
}
