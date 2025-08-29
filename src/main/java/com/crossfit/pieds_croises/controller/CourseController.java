package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.CourseCreateDTO;
import com.crossfit.pieds_croises.dto.CourseDTO;
import com.crossfit.pieds_croises.dto.CourseUpdateDTO;
import com.crossfit.pieds_croises.dto.UserDto;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
  public ResponseEntity<List<CourseDTO>> getCoursesByDay(
      @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
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
  public ResponseEntity<CourseDTO> updateCourse(@PathVariable Long id,
      @Valid @RequestBody CourseUpdateDTO courseUpdateDTO) {
    CourseDTO updateCourse = courseService.updateCourse(id, courseUpdateDTO);
    return ResponseEntity.ok(updateCourse);
  }

  @PutMapping("/{courseId}/register")
  public ResponseEntity<CourseDTO> registerToCourse(@PathVariable Long courseId, @AuthenticationPrincipal User user) {
    CourseDTO courseWithNewUser = courseService.addUserToCourse(courseId, user.getId());
    return ResponseEntity.ok(courseWithNewUser);
  }

  @DeleteMapping("/{courseId}/unsubscribe")
  public ResponseEntity<CourseDTO> unsubscribeFromCourse(@PathVariable Long courseId,
      @AuthenticationPrincipal User user) {
    CourseDTO courseMinusOneUser = courseService.deleteUserFromCourse(courseId, user.getId());
    return ResponseEntity.ok(courseMinusOneUser);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
    courseService.deleteCourse(id);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{id}/available-users")
  public ResponseEntity<List<UserDto>> getAvailableUsers(@PathVariable Long id) {
    List<UserDto> users = courseService.getUsersNotInCourse(id);
    return ResponseEntity.ok(users);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{courseId}/users/{userId}")
  public ResponseEntity<CourseDTO> addUserToCourse(@PathVariable Long courseId, @PathVariable Long userId) {
    CourseDTO course = courseService.addUserToCourse(courseId, userId);
    return ResponseEntity.ok(course);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{courseId}/users/{userId}")
  public ResponseEntity<CourseDTO> removeUserFromCourse(@PathVariable Long courseId, @PathVariable Long userId) {
    CourseDTO course = courseService.deleteUserFromCourse(courseId, userId);
    return ResponseEntity.ok(course);
  }

  @PreAuthorize("hasRole('ADMIN') or #user.id == principal.id")
  @GetMapping("/user/{userId}/weekly-count")
  public ResponseEntity<Long> getUserWeeklyCourseCount(@PathVariable Long userId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekDate) {
    Long count = courseService.getUserWeeklyCourseCount(userId, weekDate);
    return ResponseEntity.ok(count);
  }

}
