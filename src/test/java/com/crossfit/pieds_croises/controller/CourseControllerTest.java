package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.CourseCreateDTO;
import com.crossfit.pieds_croises.dto.CourseDTO;
import com.crossfit.pieds_croises.dto.CourseUpdateDTO;
import com.crossfit.pieds_croises.dto.UserDto;
import com.crossfit.pieds_croises.exception.BusinessException;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.security.CustomUserDetailsService;
import com.crossfit.pieds_croises.security.JwtAuthenticationFilter;
import com.crossfit.pieds_croises.security.JwtService;
import com.crossfit.pieds_croises.service.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourseService courseService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    public void getAllCourses() throws Exception {
        // Arrange
        CourseDTO course1 = new CourseDTO();
        course1.setTitle("Course 1");
        CourseDTO course2 = new CourseDTO();
        course2.setTitle("Course 2");

        when(courseService.getAllCourses())
                .thenReturn(List.of(course1, course2));

        // Act & Assert
        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Course 1"))
                .andExpect(jsonPath("$[1].title").value("Course 2"));
    }

    @Test
    public void testGetCoursesNextTwoWeeks() throws Exception {
        // Arrange
        CourseDTO course1 = new CourseDTO();
        course1.setTitle("Course 1");
        CourseDTO course2 = new CourseDTO();
        course2.setTitle("Course 2");

        when(courseService.getCoursesNextTwoWeeks())
                .thenReturn(List.of(course1, course2));

        // Act & Assert
        mockMvc.perform(get("/courses/next-two-weeks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Course 1"))
                .andExpect(jsonPath("$[1].title").value("Course 2"));
    }

    @Test
    public void testGetCoursesByDay() throws Exception {
        // Arrange
        LocalDate date = LocalDate.of(2025, 9, 22);
        CourseDTO course1 = new CourseDTO();
        course1.setTitle("Course 1");
        CourseDTO course2 = new CourseDTO();
        course2.setTitle("Course 2");

        when(courseService.getCoursesByDay(date))
                .thenReturn(List.of(course1, course2));

        // Act & Assert
        mockMvc.perform(get("/courses/by-day")
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Course 1"))
                .andExpect(jsonPath("$[1].title").value("Course 2"));
    }

    @Test
    public void testGetCourseById() throws Exception {
        // Arrange
        CourseDTO course = new CourseDTO();
        course.setTitle("Course 1");

        when(courseService.getCourseByID(1L))
                .thenReturn(course);

        // Act & Assert
        mockMvc.perform(get("/courses/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Course 1"));
    }

    @Test
    public void testGetCourseById_returnsNotFound_whenCourseNotFound() throws Exception {
        // Arrange
        when(courseService.getCourseByID(99L))
                .thenThrow(new ResourceNotFoundException("Course with id 99 not found"));

        // Act & Assert
        mockMvc.perform(get("/courses/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Course with id 99 not found"));
    }

    @Test
    public void testCreateCourse() throws Exception {
        // Arrange
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        CourseDTO savedCourse = new CourseDTO();
        savedCourse.setTitle("Crossfit Session");
        String json = String.format("""
        {
            "title": "Crossfit Session",
            "description": "Crossfit Session Description",
            "startDatetime": "%s",
            "duration": 60,
            "personLimit": 12,
            "coachId": 1
        }
        """, futureDate.truncatedTo(ChronoUnit.MINUTES));

        when(courseService.createCourse(any(CourseCreateDTO.class)))
                .thenReturn(savedCourse);

        // Act & Assert
        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Crossfit Session"));
    }

    @Test
    public void testCreateCourse_returnsBadRequest_whenMissingFields() throws Exception {
        // Arrange
        String json = "{}";

        // Act & Assert
        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.startDatetime").exists())
                .andExpect(jsonPath("$.duration").exists())
                .andExpect(jsonPath("$.personLimit").exists())
                .andExpect(jsonPath("$.coachId").exists());
    }

    @Test
    public void testCreateCourse_returnsBadRequest_whenCoachAlreadyBooked() throws Exception {
        // Arrange
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        String json = String.format("""
        {
            "title": "Crossfit Session",
            "description": "Crossfit Session Description",
            "startDatetime": "%s",
            "duration": 60,
            "personLimit": 12,
            "coachId": 1
        }
        """, futureDate.truncatedTo(ChronoUnit.MINUTES));

        when(courseService.createCourse(any(CourseCreateDTO.class)))
                .thenThrow(new BusinessException("A course already exists with this coach at this start date."));

        // Act & Assert
        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("A course already exists with this coach at this start date."));
    }

    @Test
    public void testUpdateCourse() throws Exception {
        // Arrange
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        CourseDTO updatedCourse = new CourseDTO();
        updatedCourse.setTitle("Updated Session");
        String json = String.format("""
        {
            "id": 1,
            "title": "Updated Session",
            "description": "Updated Session Description",
            "startDatetime": "%s",
            "duration": 60,
            "personLimit": 12,
            "coachId": 1
        }
        """, futureDate.truncatedTo(ChronoUnit.MINUTES));

        when(courseService.updateCourse(eq(1L), any(CourseUpdateDTO.class)))
                .thenReturn(updatedCourse);

        // Act & Assert
        mockMvc.perform(put("/courses/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Session"));
    }

    @Test
    public void testUpdateCourse_returnsBadRequest_whenMissingFields() throws Exception {
        // Arrange
        String json = "{}";

        // Act & Assert
        mockMvc.perform(put("/courses/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.startDatetime").exists())
                .andExpect(jsonPath("$.duration").exists())
                .andExpect(jsonPath("$.personLimit").exists())
                .andExpect(jsonPath("$.coachId").exists());
    }

    @Test
    public void testUpdateCourse_returnsNotFound_whenCourseNotFound() throws Exception {
        // Arrange
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        String json = String.format("""
        {
            "id": 99,
            "title": "Updated Session",
            "description": "Updated Session Description",
            "startDatetime": "%s",
            "duration": 60,
            "personLimit": 12,
            "coachId": 1
        }
        """, futureDate.truncatedTo(ChronoUnit.MINUTES));

        when(courseService.updateCourse(eq(99L), any(CourseUpdateDTO.class)))
                .thenThrow(new ResourceNotFoundException("Course not found with id: 99"));

        // Act & Assert
        mockMvc.perform(put("/courses/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Course not found with id: 99"));
    }

    @Test
    public void testDeleteCourse() throws Exception {
        // Arrange
        doNothing().when(courseService).deleteCourse(1L);

        // Act & Assert
        mockMvc.perform(delete("/courses/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteCourse_returnsNotFound_whenCourseNotFound() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Course not found with id: 99"))
                .when(courseService).deleteCourse(99L);

        // Act & Assert
        mockMvc.perform(delete("/courses/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Course not found with id: 99"));
    }
}