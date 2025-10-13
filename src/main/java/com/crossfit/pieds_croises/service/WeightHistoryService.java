package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.datetime.DateTimeProvider;
import com.crossfit.pieds_croises.dto.WeightHistoryDTO;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.mapper.WeightHistoryMapper;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.model.WeightHistory;
import com.crossfit.pieds_croises.repository.UserRepository;
import com.crossfit.pieds_croises.repository.WeightHistoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WeightHistoryService {

    private final WeightHistoryRepository weightHistoryRepository;
    private final WeightHistoryMapper weightHistoryMapper;
    private final UserRepository userRepository;
    private final DateTimeProvider dateTimeProvider;

    public List<WeightHistoryDTO> getAllWeightHistory(Long userId) {
        List<WeightHistory> weightHistoryList = weightHistoryRepository.findAllByUserIdOrderByDateAsc(userId);
        return weightHistoryList.stream()
                .map(weightHistoryMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<WeightHistoryDTO> getAllWeightHistoryForLastXMonths(Long userId, Integer months) {
        LocalDate startDate = dateTimeProvider.today().withDayOfMonth(1).minusMonths(months - 1);
        List<WeightHistory> weightHistoryList = weightHistoryRepository.findAllByUserIdForLastXMonths(userId, startDate);
        return weightHistoryList.stream()
                .map(weightHistoryMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    public WeightHistoryDTO createWeightHistory(WeightHistoryDTO weightHistoryDTO, Long userId) {
        WeightHistory weightHistory = weightHistoryMapper.convertToEntity(weightHistoryDTO);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found."));
        weightHistory.setUser(user);

        WeightHistory savedWeightHistory = weightHistoryRepository.save(weightHistory);
        return weightHistoryMapper.convertToDTO(savedWeightHistory);
    }

    public WeightHistoryDTO updateWeightHistory(Long id, WeightHistoryDTO weightHistoryDTO, Long userId) {
        WeightHistory weightHistory = weightHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WeightHistory with id " + id + " not found."));

        if (!weightHistory.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Not authorized to update this weightHistory.");
        }

        weightHistoryMapper.updateEntityFromDTO(weightHistoryDTO, weightHistory);
        WeightHistory updatedWeightHistory = weightHistoryRepository.save(weightHistory);
        return weightHistoryMapper.convertToDTO(updatedWeightHistory);
    }

    public void deleteWeightHistory(Long id, Long userId) {
        WeightHistory weightHistory = weightHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WeightHistory with id " + id + " not found."));
        if (!weightHistory.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Not authorized to delete this weightHistory.");
        }
        weightHistoryRepository.delete(weightHistory);
    }
}
