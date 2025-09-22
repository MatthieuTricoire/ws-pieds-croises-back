package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.CourseDTO;
import com.crossfit.pieds_croises.dto.UserDto;
import com.crossfit.pieds_croises.dto.UserUpdateDto;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    // 🔹 READ ALL
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers(
        @RequestParam(defaultValue = "false") boolean includeSubscriptions
    ) {
        List<UserDto> userDtos = userService.getAllUsers(includeSubscriptions);
        return ResponseEntity.ok(userDtos);
    }

    // 🔹 READ ONE
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto userDto = userService.getUserById(id);
        return ResponseEntity.ok(userDto);
    }

    // 🔹 READ USER PROFILE
    @GetMapping("/profile")
    public ResponseEntity<UserDto> getMyProfile(@AuthenticationPrincipal User user) {
        UserDto userDto = userService.getMyProfile(user.getId());
        return ResponseEntity.ok(userDto);
    }

    // 🔹 READ USER COURSES
    @GetMapping("/courses")
    public ResponseEntity<List<CourseDTO>> getUserCourses(@AuthenticationPrincipal User user) {
        List<CourseDTO> myCourses = userService.getUserCourses(user.getId());
        return ResponseEntity.ok(myCourses);
    }

    // 🔹 CREATE
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto user) {
        UserDto invitedUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(invitedUser);
    }

    // 🔹 UPDATE
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDto userDetails) {
        UserDto userDto = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(userDto);
    }

    // 🔹 UPDATE USER PROFILE
    @PutMapping("/profile")
    public ResponseEntity<UserDto> updateProfile(@AuthenticationPrincipal User user,
                                                 @Valid @RequestBody UserUpdateDto userDetails) {
        String username = user.getEmail();
        UserDto userDto = userService.updateProfile(username, userDetails);
        return ResponseEntity.ok(userDto);
    }

    // 🔹 DELETE
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // 🔹 DELETE USER PROFILE
    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteUserProfile(@AuthenticationPrincipal User user) {
        userService.deleteUser(user.getId());
        return ResponseEntity.noContent().build();
    }

}
