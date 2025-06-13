package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.UserDto;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserDto> getMyProfile(Authentication auth) {
        User user = (User) auth.getPrincipal();
        UserDto userDto = userService.getMyProfile(user.getId());
        if (userDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userDto);
    }

    @PutMapping
    public ResponseEntity<UserDto> updateProfile(Authentication auth, @RequestBody UserDto userDetails) {
        String username = auth.getName();
        UserDto userDto = userService.updateProfile(username, userDetails);
        if (userDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(Authentication auth) {
        User user = (User) auth.getPrincipal();
        if (userService.deleteUser(user.getId())) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}