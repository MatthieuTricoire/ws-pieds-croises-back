package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.CourseDTO;
import com.crossfit.pieds_croises.dto.UserDto;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.service.UserCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/userCourses")
public class UserCourseController {

    private final UserCourseService userCourseService;

    @PutMapping("/{courseId}/register")
    public ResponseEntity<CourseDTO> registerToCourse(@PathVariable Long courseId, @AuthenticationPrincipal User user) {
        CourseDTO courseWithNewUser = userCourseService.addUserToCourse(courseId, user.getId());
        return ResponseEntity.ok(courseWithNewUser);
    }

    @DeleteMapping("/{courseId}/unsubscribe")
    public ResponseEntity<CourseDTO> unsubscribeFromCourse(@PathVariable Long courseId,
                                                           @AuthenticationPrincipal User user) {
        CourseDTO courseMinusOneUser = userCourseService.deleteUserFromCourse(courseId, user.getId());
        return ResponseEntity.ok(courseMinusOneUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{courseId}/available-users")
    public ResponseEntity<List<UserDto>> getAvailableUsers(@PathVariable Long courseId) {
        List<UserDto> users = userCourseService.getUsersNotInCourse(courseId);
        return ResponseEntity.ok(users);
    }


    @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
    @GetMapping("/user/{userId}/weekly-count")
    public ResponseEntity<Long> getUserWeeklyCourseCount(@PathVariable Long userId,
                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekDate) {
        Long count = userCourseService.getUserWeeklyCourseCount(userId, weekDate);
        return ResponseEntity.ok(count);
    }
}
