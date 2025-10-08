package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.datetime.DateTimeProvider;
import com.crossfit.pieds_croises.dto.UserSubscriptionDto;
import com.crossfit.pieds_croises.enums.UserSubscriptionStatus;
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
        UserSubscriptionDto inputUserSubscriptionDto = new UserSubscriptionDto();
        inputUserSubscriptionDto.setUserId(userId);
        inputUserSubscriptionDto.setSubscriptionId(subscriptionId);
        UserSubscription existingSubscription = new UserSubscription(); // abonnement existant à annuler
        UserSubscription userSubscription = new UserSubscription();
        UserSubscription savedUserSubscription = new UserSubscription();
        UserSubscriptionDto expectedDto = new UserSubscriptionDto();
        User user = new User();
        Subscription subscription = new Subscription();
        subscription.setDuration((short) 5);
        subscription.setFreezeDaysAllowed((short) 3);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));
        when(dateTimeProvider.now()).thenReturn(fixedCurrentDate);
        when(userSubscriptionRepository.findTopByUserAndEndDateAfterOrderByEndDateDesc(user, fixedCurrentDate))
                .thenReturn(Optional.of(existingSubscription));
        when(userSubscriptionMapper.convertToUserSubscriptionEntity(inputUserSubscriptionDto)).thenReturn(userSubscription);
        when(userSubscriptionRepository.save(any(UserSubscription.class))).thenReturn(savedUserSubscription);
        when(userSubscriptionMapper.convertToUserSubscriptionDto(savedUserSubscription)).thenReturn(expectedDto);

        // Act
        UserSubscriptionDto result = userSubscriptionService.createUserSubscription(inputUserSubscriptionDto);

        // Assert
        assertThat(result).isEqualTo(expectedDto);
        assertThat(userSubscription.getUser()).isEqualTo(user);
        assertThat(userSubscription.getSubscription()).isEqualTo(subscription);
        assertThat(userSubscription.getStatus()).isEqualTo(UserSubscriptionStatus.ACTIVE);
        assertThat(userSubscription.getStartDate()).isEqualTo(fixedCurrentDate);
        assertThat(userSubscription.getEndDate()).isEqualTo(fixedCurrentDate.plusDays(subscription.getDuration()));
        assertThat(userSubscription.getFreezeDaysRemaining()).isEqualTo(subscription.getFreezeDaysAllowed());
        assertThat(existingSubscription.getStatus()).isEqualTo(UserSubscriptionStatus.CANCELLED);
        assertThat(existingSubscription.getEndDate()).isEqualTo(fixedCurrentDate);
        verify(userSubscriptionRepository).save(existingSubscription);
        verify(userRepository).findById(userId);
        verify(subscriptionRepository).findById(subscriptionId);
        verify(dateTimeProvider).now();
        verify(userSubscriptionRepository).findTopByUserAndEndDateAfterOrderByEndDateDesc(user, fixedCurrentDate);
        verify(userSubscriptionMapper).convertToUserSubscriptionEntity(inputUserSubscriptionDto);
        verify(userSubscriptionRepository, times(2)).save(any(UserSubscription.class));
        verify(userSubscriptionMapper).convertToUserSubscriptionDto(savedUserSubscription);
    }

    @Test
    public void testCreateUserSubscription_whenUserNotFound_shouldThrowException() {
        // Arrange
        Long userId = 1L;
        UserSubscriptionDto dto = new UserSubscriptionDto();
        dto.setUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userSubscriptionService.createUserSubscription(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
        verify(userRepository).findById(userId);
        verifyNoInteractions(subscriptionRepository, dateTimeProvider, userSubscriptionRepository, userSubscriptionMapper);
    }

    @Test
    public void testCreateUserSubscription_whenSubscriptionNotFound_shouldThrowException() {
        // Arrange
        Long userId = 1L;
        Long subscriptionId = 1L;
        UserSubscriptionDto dto = new UserSubscriptionDto();
        dto.setUserId(userId);
        dto.setSubscriptionId(subscriptionId);
        User user = new User();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userSubscriptionService.createUserSubscription(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Subscription not found");
        verify(userRepository).findById(userId);
        verify(subscriptionRepository).findById(subscriptionId);
        verifyNoInteractions(dateTimeProvider, userSubscriptionRepository, userSubscriptionMapper);
    }
}
