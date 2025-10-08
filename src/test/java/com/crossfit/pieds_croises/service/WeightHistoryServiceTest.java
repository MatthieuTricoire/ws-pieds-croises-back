package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.datetime.DateTimeProvider;
import com.crossfit.pieds_croises.dto.WeightHistoryDTO;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.mapper.WeightHistoryMapper;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.model.WeightHistory;
import com.crossfit.pieds_croises.repository.UserRepository;
import com.crossfit.pieds_croises.repository.WeightHistoryRepository;
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
public class WeightHistoryServiceTest {

    @Mock
    private WeightHistoryRepository weightHistoryRepository;

    @Mock
    private WeightHistoryMapper weightHistoryMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DateTimeProvider dateTimeProvider;

    @InjectMocks
    private WeightHistoryService weightHistoryService;

    @Test
    public void testGetAllWeightHistory() {
        // Arrange
        Long userId = 1L;
        WeightHistory weightHistory1 = new WeightHistory();
        WeightHistory weightHistory2 = new WeightHistory();
        WeightHistoryDTO weightHistoryDTO1 = new WeightHistoryDTO();
        WeightHistoryDTO weightHistoryDTO2 = new WeightHistoryDTO();
        List<WeightHistory> weightHistoryList = List.of(weightHistory1, weightHistory2);

        when(weightHistoryRepository.findAllByUserIdOrderByDateAsc(userId)).thenReturn(weightHistoryList);
        when(weightHistoryMapper.convertToDTO(weightHistory1)).thenReturn(weightHistoryDTO1);
        when(weightHistoryMapper.convertToDTO(weightHistory2)).thenReturn(weightHistoryDTO2);

        // Act
        List<WeightHistoryDTO> result = weightHistoryService.getAllWeightHistory(userId);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(weightHistoryDTO1, weightHistoryDTO2);
        verify(weightHistoryRepository, times(1)).findAllByUserIdOrderByDateAsc(userId);
        verify(weightHistoryMapper, times(1)).convertToDTO(weightHistory1);
        verify(weightHistoryMapper, times(1)).convertToDTO(weightHistory2);
    }

    @Test
    public void testGetAllWeightHistoryForLastXMonths() {
        // Arrange
        Long userId = 1L;
        Integer months = 3;
        LocalDate fixedDate = LocalDate.of(2025, 7, 17);
        LocalDate expectedStartDate = fixedDate.withDayOfMonth(1).minusMonths(months - 1);
        WeightHistory weightHistory1 = new WeightHistory();
        WeightHistory weightHistory2 = new WeightHistory();
        WeightHistoryDTO weightHistoryDTO1 = new WeightHistoryDTO();
        WeightHistoryDTO weightHistoryDTO2 = new WeightHistoryDTO();
        List<WeightHistory> weightHistoryList = List.of(weightHistory1, weightHistory2);

        when(dateTimeProvider.today()).thenReturn(fixedDate);
        when(weightHistoryRepository.findAllByUserIdForLastXMonths(userId, expectedStartDate)).thenReturn(weightHistoryList);
        when(weightHistoryMapper.convertToDTO(weightHistory1)).thenReturn(weightHistoryDTO1);
        when(weightHistoryMapper.convertToDTO(weightHistory2)).thenReturn(weightHistoryDTO2);

        // Act
        List<WeightHistoryDTO> result = weightHistoryService.getAllWeightHistoryForLastXMonths(userId, months);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(weightHistoryDTO1, weightHistoryDTO2);
        verify(dateTimeProvider, times(1)).today();
        verify(weightHistoryRepository, times(1)).findAllByUserIdForLastXMonths(userId, expectedStartDate);
        verify(weightHistoryMapper, times(1)).convertToDTO(weightHistory1);
        verify(weightHistoryMapper, times(1)).convertToDTO(weightHistory2);
    }

    @Test
    public void testCreateWeightHistory() {
        // Arrange
        Long userId = 1L;
        WeightHistoryDTO inputWeightHistoryDTO = new WeightHistoryDTO();
        WeightHistoryDTO expectedWeightHistoryDTO = new WeightHistoryDTO();
        WeightHistory  weightHistory = new WeightHistory();
        User user = new User();
        user.setId(userId);
        weightHistory.setUser(user);
        WeightHistory savedWeightHistory = new WeightHistory();

        when(weightHistoryMapper.convertToEntity(inputWeightHistoryDTO)).thenReturn(weightHistory);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(weightHistoryRepository.save(weightHistory)).thenReturn(savedWeightHistory);
        when(weightHistoryMapper.convertToDTO(savedWeightHistory)).thenReturn(expectedWeightHistoryDTO);

        // Act
        WeightHistoryDTO result = weightHistoryService.createWeightHistory(inputWeightHistoryDTO,  userId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedWeightHistoryDTO);
        assertThat(weightHistory.getUser()).isEqualTo(user);
        verify(weightHistoryMapper, times(1)).convertToEntity(inputWeightHistoryDTO);
        verify(userRepository, times(1)).findById(userId);
        verify(weightHistoryRepository, times(1)).save(weightHistory);
        verify(weightHistoryMapper, times(1)).convertToDTO(savedWeightHistory);
    }

    @Test
    public void testCreateWeightHistory_WhenUserNotFound_ShouldThrownException() {
        // Arrange
        Long userId = 1L;
        WeightHistoryDTO inputWeightHistoryDTO = new WeightHistoryDTO();
        WeightHistory weightHistory = new WeightHistory();

        when(weightHistoryMapper.convertToEntity(inputWeightHistoryDTO)).thenReturn(weightHistory);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> weightHistoryService.createWeightHistory(inputWeightHistoryDTO,  userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with id " + userId + " not found.");
        verify(weightHistoryMapper, times(1)).convertToEntity(inputWeightHistoryDTO);
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(weightHistoryMapper, userRepository);
        verifyNoInteractions(weightHistoryRepository);
    }

    @Test
    public void testUpdateWeightHistory() {
        // Arrange
        Long id = 1L;
        Long userId = 2L;
        WeightHistoryDTO inputWeightHistoryDTO = new WeightHistoryDTO();
        WeightHistoryDTO expectedWeightHistoryDTO = new WeightHistoryDTO();
        WeightHistory weightHistory = new WeightHistory();
        User user = new User();
        user.setId(userId);
        weightHistory.setUser(user);
        WeightHistory savedWeightHistory = new WeightHistory();

        when(weightHistoryRepository.findById(id)).thenReturn(Optional.of(weightHistory));
        doNothing().when(weightHistoryMapper).updateEntityFromDTO(inputWeightHistoryDTO, weightHistory);
        when(weightHistoryRepository.save(weightHistory)).thenReturn(savedWeightHistory);
        when(weightHistoryMapper.convertToDTO(savedWeightHistory)).thenReturn(expectedWeightHistoryDTO);

        // Act
        WeightHistoryDTO result = weightHistoryService.updateWeightHistory(id, inputWeightHistoryDTO,  userId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedWeightHistoryDTO);
        assertThat(weightHistory.getUser()).isEqualTo(user);
        verify(weightHistoryRepository, times(1)).findById(id);
        verify(weightHistoryMapper, times(1)).updateEntityFromDTO(inputWeightHistoryDTO, weightHistory);
        verify(weightHistoryRepository, times(1)).save(weightHistory);
        verify(weightHistoryMapper, times(1)).convertToDTO(savedWeightHistory);
    }

    @Test
    public void testUpdateWeightHistory_WhenWeightHistoryNotFound_ShouldThrownException() {
        // Arrange
        Long id = 1L;
        Long userId = 2L;
        WeightHistoryDTO inputWeightHistoryDTO = new WeightHistoryDTO();

        when(weightHistoryRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> weightHistoryService.updateWeightHistory(id, inputWeightHistoryDTO,  userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("WeightHistory with id " + id + " not found.");
        verify(weightHistoryRepository, times(1)).findById(id);
        verifyNoMoreInteractions(weightHistoryRepository);
        verifyNoInteractions(weightHistoryMapper);
    }

    @Test
    public void testUpdateWeightHistory_WhenUserIdsDoNotMatch_ShouldThrownException() {
        // Arrange
        Long id = 1L;
        Long userId = 2L;
        WeightHistoryDTO inputWeightHistoryDTO = new WeightHistoryDTO();
        WeightHistory weightHistory = new WeightHistory();
        User otherUser = new User();
        otherUser.setId(99L);
        weightHistory.setUser(otherUser);

        when(weightHistoryRepository.findById(id)).thenReturn(Optional.of(weightHistory));

        // Act & Assert
        assertThatThrownBy(() -> weightHistoryService.updateWeightHistory(id, inputWeightHistoryDTO,  userId))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Not authorized to update this weightHistory.");
        verify(weightHistoryRepository, times(1)).findById(id);
        verifyNoMoreInteractions(weightHistoryRepository);
        verifyNoInteractions(weightHistoryMapper);
    }

    @Test
    public void testDeleteWeightHistory() {
        // Arrange
        Long id = 1L;
        Long userId = 2L;
        WeightHistory weightHistory = new WeightHistory();
        User user = new User();
        user.setId(userId);
        weightHistory.setUser(user);

        when(weightHistoryRepository.findById(id)).thenReturn(Optional.of(weightHistory));

        // Act
        weightHistoryService.deleteWeightHistory(id, userId);

        // Assert
        verify(weightHistoryRepository, times(1)).findById(id);
        verify(weightHistoryRepository, times(1)).delete(weightHistory);
    }

    @Test
    public void testDeleteWeightHistory_WhenWeightHistoryNotFound_ShouldThrownException() {
        // Arrange
        Long id = 1L;
        Long userId = 2L;

        when(weightHistoryRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> weightHistoryService.deleteWeightHistory(id, userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("WeightHistory with id " + id + " not found.");
        verify(weightHistoryRepository, times(1)).findById(id);
        verifyNoMoreInteractions(weightHistoryRepository);
    }

    @Test
    public void testDeleteWeightHistory_WhenUserIdsDoNotMatch_ShouldThrownException() {
        // Arrange
        Long id = 1L;
        Long userId = 2L;
        WeightHistory weightHistory = new WeightHistory();
        User otherUser = new User();
        otherUser.setId(99L);
        weightHistory.setUser(otherUser);

        when(weightHistoryRepository.findById(id)).thenReturn(Optional.of(weightHistory));

        // Act & Assert
        assertThatThrownBy(() -> weightHistoryService.deleteWeightHistory(id, userId))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Not authorized to delete this weightHistory.");
        verify(weightHistoryRepository, times(1)).findById(id);
        verifyNoMoreInteractions(weightHistoryRepository);
    }
}
