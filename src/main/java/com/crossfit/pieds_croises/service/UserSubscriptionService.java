package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.dto.UserSubscriptionDto;
import com.crossfit.pieds_croises.exception.DuplicateResourceException;
import com.crossfit.pieds_croises.exception.ForbiddenException;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.mapper.UserSubscriptionMapper;
import com.crossfit.pieds_croises.model.Subscription;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.model.UserSubscription;
import com.crossfit.pieds_croises.repository.SubscriptionRepository;
import com.crossfit.pieds_croises.repository.UserRepository;
import com.crossfit.pieds_croises.repository.UserSubscriptionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserSubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final UserRepository userRepository;
    private final UserSubscriptionMapper userSubscriptionMapper;

    public UserSubscriptionDto createUserSubscription(UserSubscriptionDto userSubscriptionDto) {
        User user = userRepository.findById(userSubscriptionDto.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Subscription subscription = subscriptionRepository.findById(userSubscriptionDto.getSubscriptionId())
            .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        Optional<UserSubscription> existingSubscription = userSubscriptionRepository.findByUser(user);
        if (existingSubscription.isPresent()) {
            throw new DuplicateResourceException("User already has an subscription, you must update it instead of creating a new one");
        }

        UserSubscription userSubscription = userSubscriptionMapper.convertToUserSubscriptionEntity(userSubscriptionDto);
        userSubscription.setFreezeDaysRemaining(subscription.getFreezeDaysAllowed());
        userSubscription.setEndDate(LocalDateTime.now().plusDays(subscription.getDuration()));
        UserSubscription savedUserSubscription = userSubscriptionRepository.save(userSubscription);

        return userSubscriptionMapper.convertToUserSubscriptionDto(savedUserSubscription);
    }

    public UserSubscriptionDto getUserSubscription(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserSubscription userSubscription = userSubscriptionRepository.findByUser(user)
            .orElseThrow(() -> new ResourceNotFoundException("User subscription not found"));

        return userSubscriptionMapper.convertToUserSubscriptionDto(userSubscription);
    }

    public UserSubscriptionDto updateUserSubscription(Long userId, UserSubscriptionDto userSubscriptionDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!user.getId().equals(userSubscriptionDto.getUserId())) {
            throw new ForbiddenException("User ID in the DTO does not match the authenticated user ID");
        }

        UserSubscription currentUserSubscription = userSubscriptionRepository.findByUser(user)
            .orElseThrow(() -> new ResourceNotFoundException("User subscription not found"));

        Subscription newSubscription = subscriptionRepository.findById(userSubscriptionDto.getSubscriptionId())
            .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        if (currentUserSubscription.getEndDate().isAfter(LocalDateTime.now())) {
            throw new ForbiddenException("Cannot update an active subscription");
        }
        LocalDateTime today = LocalDateTime.now();
        currentUserSubscription.setSubscription(newSubscription);
        currentUserSubscription.setStartDate(today);
        currentUserSubscription.setEndDate(today.plusDays(newSubscription.getDuration()));
        currentUserSubscription.setFreezeDaysRemaining(newSubscription.getFreezeDaysAllowed());


        UserSubscription updatedUserSubscription = userSubscriptionRepository.save(currentUserSubscription);
        return userSubscriptionMapper.convertToUserSubscriptionDto(updatedUserSubscription);
    }

    public void freezeUserSubscription(Long userId, LocalDateTime freezeStartDate, LocalDateTime freezeEndDate) {
        // TODO: À déterminer si on veut que l'utilisateur ne puisse pas geler son compte alors qu'il est suspendu ?
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserSubscription userSubscription = userSubscriptionRepository.findByUser(user)
            .orElseThrow(() -> new ResourceNotFoundException("User subscription not found"));

        if (userSubscription.getEndDate().isBefore(LocalDateTime.now())) {
            throw new ForbiddenException("Cannot freeze a subscription that has already ended");
        }
        int daysToFreeze = (int) freezeEndDate.toLocalDate().toEpochDay() - (int) freezeStartDate.toLocalDate().toEpochDay();

        int remainingDays = userSubscription.getFreezeDaysRemaining();
        if (daysToFreeze > remainingDays) {
            throw new ForbiddenException("Cannot freeze more days than remaining");
        }

        userSubscription.setEndDate(userSubscription.getEndDate().plusDays(daysToFreeze));
        userSubscription.setFreezeDaysRemaining(remainingDays - daysToFreeze);
        userSubscriptionRepository.save(userSubscription);

        user.setSuspensionStartDate(freezeStartDate.toLocalDate());
        user.setSuspensionEndDate(freezeEndDate.toLocalDate());
        userRepository.save(user);

    }

    public void deleteUserSubscription(Long userSubscriptionId) {
        UserSubscription userSubscription = userSubscriptionRepository.findById(userSubscriptionId)
            .orElseThrow(() -> new ResourceNotFoundException("User subscription not found"));

        User user = userSubscription.getUser();
        user.setUserSubscriptions(null);
        user.resetSuspensionTypeAndDates();
        userSubscriptionRepository.deleteById(userSubscription.getId());
        userRepository.save(user);

    }
}
