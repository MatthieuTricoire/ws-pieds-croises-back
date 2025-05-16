package com.crossfit.pieds_croises.task;

import com.crossfit.pieds_croises.service.UserSuspensionService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ScheduledTasks {

    private UserSuspensionService userSuspensionService;

    @Scheduled(fixedRate = 86400000) // 24 hours in milliseconds
    public void checkAndResetSuspensions() {
        userSuspensionService.checkAndResetSuspensions();
    }
}
