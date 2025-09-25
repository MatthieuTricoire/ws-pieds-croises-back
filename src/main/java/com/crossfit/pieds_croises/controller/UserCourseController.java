package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.CourseDTO;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.service.UserCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
}
