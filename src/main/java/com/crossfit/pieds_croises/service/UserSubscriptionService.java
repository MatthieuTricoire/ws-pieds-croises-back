package com.crossfit.pieds_croises.service;

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
import lombok.AllArgsConstructor;
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
        LocalDateTime startDate = userSubscriptionDto.getStartDate();
        if (userSubscriptionDto.getStartDate() == null) {
            startDate = LocalDateTime.now();
        }

        Optional<UserSubscription> existingUserSubscription = userSubscriptionRepository.findByUser(user);
        existingUserSubscription.ifPresent(userSubscription -> deleteUserSubscription(userSubscription.getId()));

        // find the latest subscription for the user. Could be in the future.
//        Optional<UserSubscription> latestUserSubscriptionOpt = userSubscriptionRepository.findTopByUserAndEndDateAfterOrderByEndDateDesc(user, currentDate);
//        if (latestUserSubscriptionOpt.isPresent()) {
//            UserSubscription lastestUserSubscription = latestUserSubscriptionOpt.get();
//
//            if (lastestUserSubscription.getEndDate() == null) {
//                throw new ForbiddenException("The latest user subscription has no end date");
//            }
//
//            if (startDate != null && startDate.isBefore(lastestUserSubscription.getEndDate())) {
//                startDate = lastestUserSubscription.getEndDate().plusDays(1);
//                userSubscriptionDto.setStartDate(startDate);
//            }
//        }

        UserSubscription userSubscription = userSubscriptionMapper.convertToUserSubscriptionEntity(userSubscriptionDto);
        userSubscription.setFreezeDaysRemaining(subscription.getFreezeDaysAllowed());
        userSubscription.setStartDate(startDate);
        userSubscription.setEndDate(startDate.plusDays(subscription.getDuration()));
        UserSubscription savedUserSubscription = userSubscriptionRepository.save(userSubscription);

        return userSubscriptionMapper.convertToUserSubscriptionDto(savedUserSubscription);
    }

    public List<UserSubscriptionDto> getAllUserSubscriptionsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Optional<UserSubscription> userSubscription = userSubscriptionRepository.findByUser(user);

        if (userSubscription.isEmpty()) {
            throw new ResourceNotFoundException("No subscriptions found for user with id: " + userId);
        }

        return userSubscription.stream()
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

        UserSubscription currentUserSubscription = userSubscriptionRepository.findByUserAndStartDateBeforeAndEndDateAfter(user, currentDate, currentDate)
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found for user"));

        if (freezeStartDate.isBefore(currentDate) || freezeEndDate.isBefore(currentDate)) {
            throw new ForbiddenException("Freeze dates cannot be in the past");
        }
        if (freezeStartDate.isAfter(freezeEndDate)) {
            throw new ForbiddenException("Freeze start date cannot be after freeze end date");
        }
        int daysToFreeze = (int) freezeEndDate.toLocalDate().toEpochDay() - (int) freezeStartDate.toLocalDate().toEpochDay();

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
//        user.removeUserSubscription(userSubscription);
        user.resetSuspensionTypeAndDates();
        userSubscriptionRepository.delete(userSubscription);
        userRepository.save(user);

    }
}
