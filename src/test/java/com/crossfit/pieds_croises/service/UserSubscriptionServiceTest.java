package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.datetime.DateTimeProvider;
import com.crossfit.pieds_croises.dto.UserSubscriptionDto;
import com.crossfit.pieds_croises.exception.ForbiddenException;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.mapper.UserSubscriptionMapper;
import com.crossfit.pieds_croises.model.Subscription;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.model.UserSubscription;
import com.crossfit.pieds_croises.repository.SubscriptionRepository;
import com.crossfit.pieds_croises.repository.UserRepository;
import com.crossfit.pieds_croises.repository.UserSubscriptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserSubscriptionServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserSubscriptionRepository userSubscriptionRepository;

    @Mock
    private UserSubscriptionMapper userSubscriptionMapper;

    @Mock
    private DateTimeProvider dateTimeProvider;

    @InjectMocks
    private UserSubscriptionService userSubscriptionService;

    @Test
    public void testCreateUserSubscription() {
        // Arrange
        Long userId = 1L;
        Long subscriptionId = 1L;
        LocalDateTime fixedCurrentDate = LocalDateTime.of(2025, 7, 17, 12, 0);
        LocalDateTime fixedStartDate = LocalDateTime.of(2025, 7, 18, 12, 0);
        LocalDateTime fixedEndDate = LocalDateTime.of(2025, 7, 19, 12, 0);
        UserSubscriptionDto inputUserSubscriptionDto = new UserSubscriptionDto();
        inputUserSubscriptionDto.setUserId(userId);
        inputUserSubscriptionDto.setSubscriptionId(subscriptionId);
        inputUserSubscriptionDto.setStartDate(fixedStartDate);
        UserSubscription latestUserSubscriptionOpt = new UserSubscription();
        latestUserSubscriptionOpt.setEndDate(fixedEndDate);
        UserSubscription userSubscription = new UserSubscription();
        UserSubscription savedUserSubscription = new UserSubscription();
        UserSubscriptionDto expectedUserSubscriptionDto = new UserSubscriptionDto();
        User user = new User();
        Subscription subscription = new Subscription();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));
        when(dateTimeProvider.now()).thenReturn(fixedCurrentDate);
        when(userSubscriptionRepository.findTopByUserAndEndDateAfterOrderByEndDateDesc(user, fixedCurrentDate)).thenReturn(Optional.of(latestUserSubscriptionOpt));
        when(userSubscriptionMapper.convertToUserSubscriptionEntity(inputUserSubscriptionDto)).thenReturn(userSubscription);
        when(userSubscriptionRepository.save(userSubscription)).thenReturn(savedUserSubscription);
        when(userSubscriptionMapper.convertToUserSubscriptionDto(savedUserSubscription)).thenReturn(expectedUserSubscriptionDto);

        // Act
        UserSubscriptionDto result = userSubscriptionService.createUserSubscription(inputUserSubscriptionDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedUserSubscriptionDto);
        verify(userRepository, times(1)).findById(inputUserSubscriptionDto.getUserId());
        verify(subscriptionRepository, times(1)).findById(inputUserSubscriptionDto.getSubscriptionId());
        verify(dateTimeProvider, times(1)).now();
        verify(userSubscriptionRepository, times(1)).findTopByUserAndEndDateAfterOrderByEndDateDesc(user, fixedCurrentDate);
        verify(userSubscriptionMapper, times(1)).convertToUserSubscriptionEntity(inputUserSubscriptionDto);
        verify(userSubscriptionRepository, times(1)).save(userSubscription);
        verify(userSubscriptionMapper, times(1)).convertToUserSubscriptionDto(savedUserSubscription);
    }

    @Test
    public void testCreateUserSubscription_whenUserNotFound_shouldThrowException() {
        // Arrange
        Long userId = 1L;
        UserSubscriptionDto inputUserSubscriptionDto = new UserSubscriptionDto();
        inputUserSubscriptionDto.setUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userSubscriptionService.createUserSubscription(inputUserSubscriptionDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
        verify(userRepository, times(1)).findById(inputUserSubscriptionDto.getUserId());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(subscriptionRepository);
        verifyNoInteractions(dateTimeProvider);
        verifyNoInteractions(userSubscriptionRepository);
        verifyNoInteractions(userSubscriptionMapper);
    }

    @Test
    public void testCreateUserSubscription_whenSubscriptionNotFound_shouldThrowException() {
        // Arrange
        Long userId = 1L;
        Long subscriptionId = 1L;
        UserSubscriptionDto inputUserSubscriptionDto = new UserSubscriptionDto();
        inputUserSubscriptionDto.setUserId(userId);
        inputUserSubscriptionDto.setSubscriptionId(subscriptionId);
        User user = new User();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userSubscriptionService.createUserSubscription(inputUserSubscriptionDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Subscription not found");
        verify(userRepository, times(1)).findById(inputUserSubscriptionDto.getUserId());
        verify(subscriptionRepository, times(1)).findById(inputUserSubscriptionDto.getSubscriptionId());
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(subscriptionRepository);
        verifyNoInteractions(dateTimeProvider);
        verifyNoInteractions(userSubscriptionRepository);
        verifyNoInteractions(userSubscriptionMapper);
    }

    @Test
    public void testCreateUserSubscription_whenStartDateIsBeforeCurrentDate_shouldThrowException() {
        // Arrange
        Long userId = 1L;
        Long subscriptionId = 1L;
        LocalDateTime fixedCurrentDate = LocalDateTime.of(2025, 7, 17, 12, 0);
        LocalDateTime fixedStartDate = LocalDateTime.of(2025, 7, 16, 12, 0);
        UserSubscriptionDto inputUserSubscriptionDto = new UserSubscriptionDto();
        inputUserSubscriptionDto.setUserId(userId);
        inputUserSubscriptionDto.setSubscriptionId(subscriptionId);
        inputUserSubscriptionDto.setStartDate(fixedStartDate);
        User user = new User();
        Subscription subscription = new Subscription();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));
        when(dateTimeProvider.now()).thenReturn(fixedCurrentDate);

        // Act & Assert
        assertThatThrownBy(() -> userSubscriptionService.createUserSubscription(inputUserSubscriptionDto))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Start date cannot be in the past");
        verify(userRepository, times(1)).findById(userId);
        verify(subscriptionRepository, times(1)).findById(subscriptionId);
        verify(dateTimeProvider, times(1)).now();
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(subscriptionRepository);
        verifyNoMoreInteractions(dateTimeProvider);
        verifyNoInteractions(userSubscriptionRepository);
        verifyNoInteractions(userSubscriptionMapper);
    }
}
