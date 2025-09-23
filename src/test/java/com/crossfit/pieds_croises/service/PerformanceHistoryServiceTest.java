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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class PerformanceHistoryServiceTest {

    @Mock
    private PerformanceHistoryRepository performanceHistoryRepository;

    @Mock
    private PerformanceHistoryMapper performanceHistoryMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private DateTimeProvider dateTimeProvider;

    @InjectMocks
    private PerformanceHistoryService performanceHistoryService;

    @Test
    public void testGetAllPerformanceHistory() {
        // Arrange
        Long userId = 1L;
        PerformanceHistory performanceHistory1 = new PerformanceHistory();
        PerformanceHistory performanceHistory2 = new PerformanceHistory();
        PerformanceHistoryDTO performanceHistoryDTO1 = new PerformanceHistoryDTO();
        PerformanceHistoryDTO performanceHistoryDTO2 = new PerformanceHistoryDTO();
        List<PerformanceHistory> performanceHistoryList = List.of(performanceHistory1, performanceHistory2);

        when(performanceHistoryRepository.findAllByUserIdOrderByDateAsc(userId)).thenReturn(performanceHistoryList);
        when(performanceHistoryMapper.convertToDTO(performanceHistory1)).thenReturn(performanceHistoryDTO1);
        when(performanceHistoryMapper.convertToDTO(performanceHistory2)).thenReturn(performanceHistoryDTO2);

        // Act
        List<PerformanceHistoryDTO> result = performanceHistoryService.getAllPerformanceHistory(userId);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(performanceHistoryDTO1, performanceHistoryDTO2);
        verify(performanceHistoryRepository, times(1)).findAllByUserIdOrderByDateAsc(userId);
        verify(performanceHistoryMapper, times(1)).convertToDTO(performanceHistory1);
        verify(performanceHistoryMapper, times(1)).convertToDTO(performanceHistory2);
    }

    @Test
    public void testGetAllPerformanceHistoryForLastXMonths() {
        // Arrange
        Long userId = 1L;
        Integer months = 3;
        LocalDate fixedDate = LocalDate.of(2025, 7, 16);
        LocalDate expectedStartDate = fixedDate.withDayOfMonth(1).minusMonths(months - 1);
        PerformanceHistory performanceHistory1 = new PerformanceHistory();
        PerformanceHistory performanceHistory2 = new PerformanceHistory();
        List<PerformanceHistory> performanceHistoryList = List.of(performanceHistory1, performanceHistory2);
        PerformanceHistoryDTO performanceHistoryDTO1 = new PerformanceHistoryDTO();
        PerformanceHistoryDTO performanceHistoryDTO2 = new PerformanceHistoryDTO();

        when(dateTimeProvider.today()).thenReturn(fixedDate);
        when(performanceHistoryRepository.findAllByUserIdForLastXMonths(userId, expectedStartDate)).thenReturn(performanceHistoryList);
        when(performanceHistoryMapper.convertToDTO(performanceHistory1)).thenReturn(performanceHistoryDTO1);
        when(performanceHistoryMapper.convertToDTO(performanceHistory2)).thenReturn(performanceHistoryDTO2);

        // Act
        List<PerformanceHistoryDTO> result = performanceHistoryService.getAllPerformanceHistoryForLastXMonths(userId, months);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(performanceHistoryDTO1, performanceHistoryDTO2);
        verify(dateTimeProvider, times(1)).today();
        verify(performanceHistoryRepository, times(1)).findAllByUserIdForLastXMonths(userId, expectedStartDate);
        verify(performanceHistoryMapper, times(1)).convertToDTO(performanceHistory1);
        verify(performanceHistoryMapper, times(1)).convertToDTO(performanceHistory2);
    }

    @Test
    public void testCreatePerformanceHistory() {
        // Arrange
        Long userId = 1L;
        Long exerciseId = 2L;
        PerformanceHistory performanceHistory = new PerformanceHistory();
        PerformanceHistory savedPerformanceHistory = new PerformanceHistory();
        PerformanceHistoryDTO inputDTO = new PerformanceHistoryDTO();
        inputDTO.setExerciseId(exerciseId);
        PerformanceHistoryDTO expectedDTO = new PerformanceHistoryDTO();
        User user = new User();
        Exercice exercise = new Exercice();

        when(performanceHistoryMapper.convertToEntity(inputDTO)).thenReturn(performanceHistory);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        when(performanceHistoryRepository.save(performanceHistory)).thenReturn(savedPerformanceHistory);
        when(performanceHistoryMapper.convertToDTO(savedPerformanceHistory)).thenReturn(expectedDTO);

        // Act
        PerformanceHistoryDTO result = performanceHistoryService.createPerformanceHistory(inputDTO, userId);

        // Assert
        assertThat(result).isEqualTo(expectedDTO);
        assertThat(performanceHistory.getUser()).isEqualTo(user);
        assertThat(performanceHistory.getExercice()).isEqualTo(exercise);
        verify(performanceHistoryMapper, times(1)).convertToEntity(inputDTO);
        verify(userRepository, times(1)).findById(userId);
        verify(exerciseRepository, times(1)).findById(exerciseId);
        verify(performanceHistoryRepository, times(1)).save(performanceHistory);
        verify(performanceHistoryMapper, times(1)).convertToDTO(savedPerformanceHistory);
    }

    @Test
    public void testCreatePerformanceHistory_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        Long userId = 1L;
        PerformanceHistoryDTO inputDTO = new PerformanceHistoryDTO();
        PerformanceHistory performanceHistory = new PerformanceHistory();

        when(performanceHistoryMapper.convertToEntity(inputDTO)).thenReturn(performanceHistory);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> performanceHistoryService.createPerformanceHistory(inputDTO, userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with id " + userId + " not found.");
        verify(performanceHistoryMapper, times(1)).convertToEntity(inputDTO);
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(performanceHistoryMapper,  userRepository);
        verifyNoInteractions(exerciseRepository);
        verifyNoInteractions(performanceHistoryRepository);
    }

    @Test
    public void testCreatePerformanceHistory_WhenExerciseNotFound_ShouldThrowException() {
        // Arrange
        Long userId = 1L;
        Long exerciseId = 2L;
        PerformanceHistoryDTO inputDTO = new PerformanceHistoryDTO();
        inputDTO.setExerciseId(exerciseId);
        PerformanceHistory performanceHistory = new PerformanceHistory();
        User user = new User();

        when(performanceHistoryMapper.convertToEntity(inputDTO)).thenReturn(performanceHistory);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> performanceHistoryService.createPerformanceHistory(inputDTO, userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Exercice with id " + exerciseId + " not found.");
        verify(performanceHistoryMapper, times(1)).convertToEntity(inputDTO);
        verify(userRepository, times(1)).findById(userId);
        verify(exerciseRepository, times(1)).findById(exerciseId);
        verifyNoMoreInteractions(performanceHistoryMapper, userRepository, exerciseRepository);
        verifyNoInteractions(performanceHistoryRepository);
    }

    @Test
    public void testUpdatePerformanceHistory() {
        // Arrange
        Long id = 1L;
        Long userId = 2L;
        Long exerciseId = 3L;
        PerformanceHistoryDTO inputDTO = new PerformanceHistoryDTO();
        inputDTO.setExerciseId(exerciseId);
        PerformanceHistoryDTO expectedDTO = new PerformanceHistoryDTO();
        PerformanceHistory performanceHistory = new PerformanceHistory();
        PerformanceHistory savedPerformanceHistory = new PerformanceHistory();
        User user = new User();
        user.setId(userId);
        performanceHistory.setUser(user);
        Exercice exercise = new Exercice();

        when(performanceHistoryRepository.findById(id)).thenReturn(Optional.of(performanceHistory));
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        doNothing().when(performanceHistoryMapper).updateEntityFromDTO(inputDTO, performanceHistory);
        when(performanceHistoryRepository.save(performanceHistory)).thenReturn(savedPerformanceHistory);
        when(performanceHistoryMapper.convertToDTO(savedPerformanceHistory)).thenReturn(expectedDTO);

        // Act
        PerformanceHistoryDTO result = performanceHistoryService.updatePerformanceHistory(id, inputDTO, userId);

        // Assert
        assertThat(result).isEqualTo(expectedDTO);
        assertThat(performanceHistory.getExercice()).isEqualTo(exercise);
        verify(performanceHistoryRepository, times(1)).findById(id);
        verify(exerciseRepository, times(1)).findById(exerciseId);
        verify(performanceHistoryMapper, times(1)).updateEntityFromDTO(inputDTO, performanceHistory);
        verify(performanceHistoryRepository, times(1)).save(performanceHistory);
        verify(performanceHistoryMapper, times(1)).convertToDTO(savedPerformanceHistory);
    }

    @Test
    public void testUpdatePerformanceHistory_WhenPerformanceHistoryNotFound_ShouldThrowException() {
        // Arrange
        Long id = 1L;
        Long userId = 2L;
        PerformanceHistoryDTO inputDTO = new PerformanceHistoryDTO();

        when(performanceHistoryRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> performanceHistoryService.updatePerformanceHistory(id, inputDTO, userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("PerformanceHistory with id " + id + " not found.");
        verify(performanceHistoryRepository, times(1)).findById(id);
        verifyNoMoreInteractions(performanceHistoryRepository);
        verifyNoInteractions(exerciseRepository);
        verifyNoInteractions(performanceHistoryMapper);
    }

    @Test
    public void testUpdatePerformanceHistory_WhenUserIdsDoNotMatch_ShouldThrowException() {
        // Arrange
        Long id = 1L;
        Long userId = 2L;
        PerformanceHistoryDTO inputDTO = new PerformanceHistoryDTO();
        PerformanceHistory  performanceHistory = new PerformanceHistory();
        User otherUser = new User();
        otherUser.setId(99L);
        performanceHistory.setUser(otherUser);

        when(performanceHistoryRepository.findById(id)).thenReturn(Optional.of(performanceHistory));

        // Act & Assert
        assertThatThrownBy(() -> performanceHistoryService.updatePerformanceHistory(id, inputDTO, userId))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Not authorized to update this performanceHistory.");
        verify(performanceHistoryRepository, times(1)).findById(id);
        verifyNoMoreInteractions(performanceHistoryRepository);
        verifyNoInteractions(exerciseRepository);
        verifyNoInteractions(performanceHistoryMapper);
    }

    @Test
    public void testUpdatePerformanceHistory_WhenExerciseNotFound_ShouldThrowException() {
        // Arrange
        Long id = 1L;
        Long userId = 2L;
        Long exerciseId = 3L;
        PerformanceHistoryDTO inputDTO = new PerformanceHistoryDTO();
        inputDTO.setExerciseId(exerciseId);
        PerformanceHistory  performanceHistory = new PerformanceHistory();
        User user = new User();
        user.setId(userId);
        performanceHistory.setUser(user);

        when(performanceHistoryRepository.findById(id)).thenReturn(Optional.of(performanceHistory));
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> performanceHistoryService.updatePerformanceHistory(id, inputDTO, userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Exercice with id " + exerciseId + " not found.");
        verify(performanceHistoryRepository, times(1)).findById(id);
        verify(exerciseRepository, times(1)).findById(exerciseId);
        verifyNoMoreInteractions(performanceHistoryRepository, exerciseRepository);
        verifyNoInteractions(performanceHistoryMapper);
    }

    @Test
    public void testDeletePerformanceHistory() {
        // Arrange
        Long id = 1L;
        Long userId = 2L;
        PerformanceHistory  performanceHistory = new PerformanceHistory();
        User user = new User();
        user.setId(userId);
        performanceHistory.setUser(user);

        when(performanceHistoryRepository.findById(id)).thenReturn(Optional.of(performanceHistory));

        // Act
        performanceHistoryService.deletePerformanceHistory(id, userId);

        // Assert
        verify(performanceHistoryRepository, times(1)).findById(id);
        verify(performanceHistoryRepository, times(1)).delete(performanceHistory);
    }

    @Test
    public void testDeletePerformanceHistory_WhenPerformanceHistoryNotFound_ShouldThrowException() {
        // Arrange
        Long id = 1L;
        Long userId = 2L;

        when(performanceHistoryRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> performanceHistoryService.deletePerformanceHistory(id, userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("PerformanceHistory with id " + id + " not found.");
        verify(performanceHistoryRepository, times(1)).findById(id);
        verifyNoMoreInteractions(performanceHistoryRepository);
    }

    @Test
    public void testDeletePerformanceHistory_WhenUserIdsDoNotMatch_ShouldThrowException() {
        // Arrange
        Long id = 1L;
        Long userId = 2L;
        PerformanceHistory performanceHistory = new PerformanceHistory();
        User otherUser = new User();
        otherUser.setId(99L);
        performanceHistory.setUser(otherUser);

        when(performanceHistoryRepository.findById(id)).thenReturn(Optional.of(performanceHistory));

        // Act & Assert
        assertThatThrownBy(() -> performanceHistoryService.deletePerformanceHistory(id, userId))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Not authorized to delete this performanceHistory.");
        verify(performanceHistoryRepository, times(1)).findById(id);
        verifyNoMoreInteractions(performanceHistoryRepository);
    }
}
