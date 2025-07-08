package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.UserDto;
import com.crossfit.pieds_croises.dto.UserUpdateDto;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserDto> getMyProfile(@AuthenticationPrincipal User user) {
        UserDto userDto = userService.getMyProfile(user.getId());
        return ResponseEntity.ok(userDto);
    }

    @PutMapping
    public ResponseEntity<UserDto> updateProfile(@AuthenticationPrincipal User user, @Valid @RequestBody UserUpdateDto userDetails) {
        String username = user.getEmail();
        UserDto userDto = userService.updateProfile(username, userDetails);
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal User user) {
        userService.deleteUser(user.getId());
        return ResponseEntity.noContent().build();
    }


}