package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.enums.SuspensionType;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Service
public class UserSuspensionService {
    // TODO: Récupérer ses valeurs depuis la table de configuration de la box
    private static final int MAX_STRIKES = 5;
    private static final int SUSPENSION_DAYS = 7;
    private final UserRepository userRepository;

    public void applyStrike(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.incrementStrikeCount();
        if (user.getStrikeCount() >= MAX_STRIKES) {
            user.applyPenaltySuspension(SUSPENSION_DAYS);
        }
        userRepository.save(user);
    }

    public void removeStrike(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.decrementStrikeCount();
        user.resetSuspensionTypeAndDates();
        user.resetStrikeCount();
        userRepository.save(user);
    }

    public void checkAndResetSuspensions() {
        List<User> users = userRepository.findAll();
        LocalDate today = LocalDate.now();
        for (User user : users) {
            LocalDate start = user.getSuspensionStartDate();
            LocalDate end = user.getSuspensionEndDate();
            SuspensionType suspensionType = user.getSuspensionType();

            boolean hasSuspensionDates = start != null && end != null;

            if (hasSuspensionDates && today.isAfter(end)) {
                if (suspensionType == SuspensionType.PENALTY) {
                    user.resetSuspensionTypeAndDates();
                    user.resetStrikeCount();

                } else if (suspensionType == SuspensionType.HOLIDAY) {
                    user.resetSuspensionTypeAndDates();
                }
            } else if (hasSuspensionDates && suspensionType == null) {
                user.setSuspensionType(SuspensionType.HOLIDAY);

            }
            userRepository.save(user);
        }
    }

}
