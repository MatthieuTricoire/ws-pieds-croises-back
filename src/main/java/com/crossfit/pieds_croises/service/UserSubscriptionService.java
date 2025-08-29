package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.dto.UserSubscriptionDto;
import com.crossfit.pieds_croises.enums.UserSubscriptionStatus;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        LocalDateTime currentDate = LocalDateTime.now();

        // Vérifier le dernier abonnement actif
        Optional<UserSubscription> existingUserSubscription = userSubscriptionRepository
            .findTopByUserAndEndDateAfterOrderByEndDateDesc(user, currentDate);

        if (existingUserSubscription.isPresent()) {
            UserSubscription currentSubscription = existingUserSubscription.get();

            currentSubscription.setEndDate(currentDate);
            currentSubscription.setStatus(UserSubscriptionStatus.CANCELLED);
            userSubscriptionRepository.save(currentSubscription);
        }

        UserSubscription userSubscription = userSubscriptionMapper.convertToUserSubscriptionEntity(userSubscriptionDto);
        userSubscription.setUser(user);
        userSubscription.setSubscription(subscription);
        userSubscription.setFreezeDaysRemaining(subscription.getFreezeDaysAllowed());
        userSubscription.setStartDate(currentDate);
        userSubscription.setStatus(UserSubscriptionStatus.ACTIVE);
        userSubscription.setEndDate(currentDate.plusDays(subscription.getDuration()));

        UserSubscription savedUserSubscription = userSubscriptionRepository.save(userSubscription);

        return userSubscriptionMapper.convertToUserSubscriptionDto(savedUserSubscription);
    }

    public UserSubscriptionDto getActiveUserSubscription(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        UserSubscription activeUserSubscription = userSubscriptionRepository
            .findByUserAndStatus(user, UserSubscriptionStatus.ACTIVE)
            .orElseThrow(() -> new ResourceNotFoundException("No active subscription found for user " + userId));

        return userSubscriptionMapper.convertToUserSubscriptionDto(activeUserSubscription);
    }

    public List<UserSubscriptionDto> getAllUserSubscriptionsByUserId(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<UserSubscription> userSubscriptions = userSubscriptionRepository.findByUser(user);

        if (userSubscriptions.isEmpty()) {
            throw new ResourceNotFoundException("No subscriptions found for user with id: " + userId);
        }

        return userSubscriptions.stream()
            .map(userSubscriptionMapper::convertToUserSubscriptionDto)
            .collect(Collectors.toList());
    }

    public UserSubscriptionDto getUserSubscriptionById(Long userSubscriptionId) {
        UserSubscription userSubscription = userSubscriptionRepository.findById(userSubscriptionId)
            .orElseThrow(() -> new ResourceNotFoundException("User subscription not found"));

        return userSubscriptionMapper.convertToUserSubscriptionDto(userSubscription);
    }

    public void freezeUserSubscription(Long userId, LocalDateTime freezeStartDate, LocalDateTime freezeEndDate) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LocalDateTime currentDate = LocalDateTime.now();

        UserSubscription currentUserSubscription = userSubscriptionRepository
            .findByUserAndStartDateBeforeAndEndDateAfter(user, currentDate, currentDate)
            .orElseThrow(() -> new ResourceNotFoundException("No active subscription found for user"));

        if (freezeStartDate.isBefore(currentDate) || freezeEndDate.isBefore(currentDate)) {
            throw new ForbiddenException("Freeze dates cannot be in the past");
        }
        if (freezeStartDate.isAfter(freezeEndDate)) {
            throw new ForbiddenException("Freeze start date cannot be after freeze end date");
        }
        int daysToFreeze = (int) freezeEndDate.toLocalDate().toEpochDay()
            - (int) freezeStartDate.toLocalDate().toEpochDay();

        int remainingDays = currentUserSubscription.getFreezeDaysRemaining();

        if (daysToFreeze > remainingDays) {
            throw new ForbiddenException("Cannot freeze more days than remaining");
        }

        currentUserSubscription.setEndDate(currentUserSubscription.getEndDate().plusDays(daysToFreeze));
        currentUserSubscription.setFreezeDaysRemaining(remainingDays - daysToFreeze);
        userSubscriptionRepository.save(currentUserSubscription);

        user.setSuspensionStartDate(freezeStartDate.toLocalDate());
        user.setSuspensionEndDate(freezeEndDate.toLocalDate());
        userRepository.save(user);
    }

    public void deleteUserSubscription(Long userSubscriptionId) {
        UserSubscription userSubscription = userSubscriptionRepository.findById(userSubscriptionId)
            .orElseThrow(() -> new ResourceNotFoundException("User subscription not found"));

        User user = userSubscription.getUser();
        user.resetSuspensionTypeAndDates();
        userSubscriptionRepository.delete(userSubscription);
        userRepository.save(user);
    }

    public void cancelUserSubscription(Long userSubscriptionId) {
        UserSubscription userSubscription = userSubscriptionRepository.findById(userSubscriptionId)
            .orElseThrow(() -> new ResourceNotFoundException("User subscription not found"));

        userSubscription.setStatus(UserSubscriptionStatus.CANCELLED);
        userSubscriptionRepository.save(userSubscription);
    }

    public boolean isOwnerOfSubscription(Long userSubscriptionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Long currentUserId = currentUser.getId();

        return userSubscriptionRepository.findById(userSubscriptionId)
            .map(UserSubscription::getUser)
            .map(User::getId)
            .map(id -> id.equals(currentUserId))
            .orElse(false);
    }


}
