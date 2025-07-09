package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.CourseCreateDTO;
import com.crossfit.pieds_croises.dto.CourseDTO;
import com.crossfit.pieds_croises.dto.CourseUpdateDTO;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        List<CourseDTO> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/next-two-weeks")
    public ResponseEntity<List<CourseDTO>> getCoursesNextTwoWeeks() {
        List<CourseDTO> courses = courseService.getCoursesNextTwoWeeks();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/by-day")
    public ResponseEntity<List<CourseDTO>> getCoursesByDay(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<CourseDTO> courses = courseService.getCoursesByDay(date);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        CourseDTO courseDTO = courseService.getCourseByID(id);
        return ResponseEntity.ok(courseDTO);
    }

    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(@Valid @RequestBody CourseCreateDTO courseCreateDTO) {
        CourseDTO savedCourse = courseService.createCourse(courseCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCourse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseUpdateDTO courseUpdateDTO) {
        CourseDTO updateCourse = courseService.updateCourse(id, courseUpdateDTO);
        return ResponseEntity.ok(updateCourse);
    }

    @PutMapping("/{courseId}/register")
    public ResponseEntity<CourseDTO> addUserToCourse(@PathVariable Long courseId, @AuthenticationPrincipal User user) {
        CourseDTO courseWithNewUser = courseService.addUserToCourse(courseId, user.getId());
        return ResponseEntity.ok(courseWithNewUser);
    }

    @DeleteMapping("/{courseId}/unsubscribe")
    public ResponseEntity<CourseDTO> deleteUserFromCourse(@PathVariable Long courseId, @AuthenticationPrincipal User user) {
        CourseDTO courseMinusOneUser = courseService.deleteUserFromCourse(courseId, user.getId());
        return ResponseEntity.ok(courseMinusOneUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
