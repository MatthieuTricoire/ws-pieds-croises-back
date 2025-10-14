package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.dto.SubscriptionCreateDto;
import com.crossfit.pieds_croises.dto.SubscriptionDto;
import com.crossfit.pieds_croises.exception.DuplicateResourceException;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.mapper.SubscriptionMapper;
import com.crossfit.pieds_croises.model.Box;
import com.crossfit.pieds_croises.model.Subscription;
import com.crossfit.pieds_croises.repository.BoxRepository;
import com.crossfit.pieds_croises.repository.SubscriptionRepository;
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
public class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriptionMapper subscriptionMapper;

    @Mock
    private BoxRepository boxRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    public void testAddSubscription() {
        // Arrange
        SubscriptionCreateDto inputSubscriptionDto = new SubscriptionCreateDto();
        SubscriptionDto expectedSubscriptionDto = new SubscriptionDto();
        inputSubscriptionDto.setName("Test subscription");
        inputSubscriptionDto.setPrice(150);
        inputSubscriptionDto.setSessionPerWeek(2);
        inputSubscriptionDto.setDuration((short) 30);
        Subscription subscription = new Subscription();
        Subscription savedSubscription = new Subscription();

        when(subscriptionRepository.existsByName(inputSubscriptionDto.getName())).thenReturn(false);
        when(subscriptionRepository.existsByPrice(inputSubscriptionDto.getPrice())).thenReturn(false);
        when(subscriptionMapper.convertToSubscriptionEntity(inputSubscriptionDto)).thenReturn(subscription);
        when(subscriptionRepository.save(subscription)).thenReturn(savedSubscription);
        when(subscriptionMapper.convertToSubscriptionDto(savedSubscription)).thenReturn(expectedSubscriptionDto);

        // Act
        SubscriptionDto result = subscriptionService.addSubscription(inputSubscriptionDto);

        // Assert
        assertThat(result).isEqualTo(expectedSubscriptionDto);
        verify(subscriptionRepository, times(1)).existsByName(inputSubscriptionDto.getName());
        verify(subscriptionRepository, times(1)).existsByPrice(inputSubscriptionDto.getPrice());
        verify(subscriptionMapper, times(1)).convertToSubscriptionEntity(inputSubscriptionDto);
        verify(subscriptionRepository, times(1)).save(subscription);
        verify(subscriptionMapper, times(1)).convertToSubscriptionDto(savedSubscription);
    }

    @Test
    public void testAddSubscription_WhenSubscriptionNameExists_ShouldThrownException() {
        // Arrange
        SubscriptionCreateDto inputSubscriptionDto = new SubscriptionCreateDto();
        inputSubscriptionDto.setName("Test subscription - name already exists");
        inputSubscriptionDto.setPrice(150);
        inputSubscriptionDto.setSessionPerWeek(2);
        inputSubscriptionDto.setDuration((short) 30);

        when(subscriptionRepository.existsByName(inputSubscriptionDto.getName())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> subscriptionService.addSubscription(inputSubscriptionDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("A subscription with this name already exists");
        verify(subscriptionRepository, times(1)).existsByName(inputSubscriptionDto.getName());
        verifyNoMoreInteractions(boxRepository, subscriptionRepository);
        verifyNoInteractions(subscriptionMapper);
    }

    @Test
    public void testAddSubscription_WhenSubscriptionPriceExists_ShouldThrownException() {
        // Arrange
        SubscriptionCreateDto inputSubscriptionDto = new SubscriptionCreateDto();
        inputSubscriptionDto.setName("Test subscription");
        inputSubscriptionDto.setPrice(150);
        inputSubscriptionDto.setSessionPerWeek(2);
        inputSubscriptionDto.setDuration((short) 30);

        when(subscriptionRepository.existsByName(inputSubscriptionDto.getName())).thenReturn(false);
        when(subscriptionRepository.existsByPrice(inputSubscriptionDto.getPrice())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> subscriptionService.addSubscription(inputSubscriptionDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("A subscription with this price already exists");
        verify(subscriptionRepository, times(1)).existsByName(inputSubscriptionDto.getName());
        verify(subscriptionRepository, times(1)).existsByPrice(inputSubscriptionDto.getPrice());
        verifyNoMoreInteractions(boxRepository, subscriptionRepository);
        verifyNoInteractions(subscriptionMapper);
    }

    @Test
    public void testGetSubscriptionById() {
        // Arrange
        Long id = 1L;
        Subscription subscription = new Subscription();
        SubscriptionDto expectedSubscriptionDto = new SubscriptionDto();

        when(subscriptionRepository.findById(id)).thenReturn(Optional.of(subscription));
        when(subscriptionMapper.convertToSubscriptionDto(subscription)).thenReturn(expectedSubscriptionDto);

        // Act
        SubscriptionDto result = subscriptionService.getSubscriptionById(id);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedSubscriptionDto);
        verify(subscriptionRepository, times(1)).findById(id);
        verify(subscriptionMapper, times(1)).convertToSubscriptionDto(subscription);
    }

    @Test
    public void testGetSubscriptionById_WhenSubscriptionNotFound_ShouldThrownException() {
        // Arrange
        Long id = 1L;

        when(subscriptionRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> subscriptionService.getSubscriptionById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Subscription not found");
        verify(subscriptionRepository, times(1)).findById(id);
        verifyNoMoreInteractions(subscriptionRepository);
        verifyNoInteractions(subscriptionMapper);
    }

    @Test
    public void testUpdateSubscription() {
        // Arrange
        Long id = 1L;
        SubscriptionDto inputSubscriptionDto = new SubscriptionDto();
        SubscriptionDto expectedSubscriptionDto = new SubscriptionDto();
        Subscription subscription = new Subscription();
        Subscription savedSubscription = new Subscription();

        when(subscriptionRepository.findById(id)).thenReturn(Optional.of(subscription));
        doNothing().when(subscriptionMapper).updateSubscriptionFromDto(inputSubscriptionDto, subscription);
        when(subscriptionRepository.save(subscription)).thenReturn(savedSubscription);
        when(subscriptionMapper.convertToSubscriptionDto(savedSubscription)).thenReturn(expectedSubscriptionDto);

        // Act
        SubscriptionDto result = subscriptionService.updateSubscription(id, inputSubscriptionDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedSubscriptionDto);
        verify(subscriptionRepository, times(1)).findById(id);
        verify(subscriptionMapper, times(1)).updateSubscriptionFromDto(inputSubscriptionDto, subscription);
        verify(subscriptionRepository, times(1)).save(subscription);
        verify(subscriptionMapper, times(1)).convertToSubscriptionDto(savedSubscription);
    }

    @Test
    public void testUpdateSubscription_WhenSubscriptionNotFound_ShouldThrownException() {
        // Arrange
        Long id = 1L;
        SubscriptionDto inputSubscriptionDto = new SubscriptionDto();

        when(subscriptionRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> subscriptionService.updateSubscription(id, inputSubscriptionDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Subscription not found");
        verify(subscriptionRepository, times(1)).findById(id);
        verifyNoMoreInteractions(subscriptionRepository);
        verifyNoInteractions(subscriptionMapper);
    }

    @Test
    public void testDeleteSubscription() {
        // Arrange
        Long id = 1L;
        Subscription subscription = new Subscription();

        when(subscriptionRepository.findById(id)).thenReturn(Optional.of(subscription));

        // Act
        subscriptionService.deleteSubscription(id);

        // Assert
        verify(subscriptionRepository, times(1)).findById(id);
        verify(subscriptionRepository, times(1)).delete(subscription);
    }

    @Test
    public void testDeleteSubscription_WhenSubscriptionNotFound_ShouldThrownException() {
        // Arrange
        Long id = 1L;

        when(subscriptionRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> subscriptionService.deleteSubscription(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Subscription not found");
        verify(subscriptionRepository, times(1)).findById(id);
        verifyNoMoreInteractions(subscriptionRepository);
    }

}
