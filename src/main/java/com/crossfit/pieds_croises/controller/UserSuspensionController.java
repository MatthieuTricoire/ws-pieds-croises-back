package com.crossfit.pieds_croises.controller;


import com.crossfit.pieds_croises.service.UserSuspensionService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
    @RequestMapping("/users")
    public class UserSuspensionController {

    private UserSuspensionService userPenaltyService;

        @PutMapping("/{userId}/increase-strike")
        public void applyStrike(@PathVariable Long userId) {
            userPenaltyService.applyStrike(userId);
        }


        @PutMapping ("/check-strikes")
        public void checkAndResetStrikes() {
            userPenaltyService.checkAndResetSuspensions();
        }

        @PutMapping("/{userId}/decrease-strike")
        public void removeStrike(@PathVariable Long userId) {
            userPenaltyService.removeStrike(userId);
        }
    }

