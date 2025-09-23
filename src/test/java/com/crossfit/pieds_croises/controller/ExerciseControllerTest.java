package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.ExerciceDTO;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.model.Exercice;
import com.crossfit.pieds_croises.security.CustomUserDetailsService;
import com.crossfit.pieds_croises.security.JwtAuthenticationFilter;
import com.crossfit.pieds_croises.security.JwtService;
import com.crossfit.pieds_croises.service.ExerciseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExerciseController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class ExerciseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExerciseService exerciseService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    public void getAllExercises() throws Exception {
        // Arrange
        ExerciceDTO exerciseDTO1 = new ExerciceDTO();
        exerciseDTO1.setName("Exercise 1");
        ExerciceDTO exerciseDTO2 = new ExerciceDTO();
        exerciseDTO2.setName("Exercise 2");

        when(exerciseService.getAllExercises())
                .thenReturn(List.of(exerciseDTO1, exerciseDTO2));

        // Act & Assert
        mockMvc.perform(get("/exercises"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Exercise 1"))
                .andExpect(jsonPath("$[1].name").value("Exercise 2"));
    }

    @Test
    public void testGetAllExercises_returnsNoContent_whenNoExercise() throws Exception {
        // Arrange
        when(exerciseService.getAllExercises())
                .thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/exercises"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetExerciseById() throws Exception {
        // Arrange
        ExerciceDTO exerciseDTO = new ExerciceDTO();
        exerciseDTO.setName("Exercise 1");

        when(exerciseService.getExerciseById(1L))
                .thenReturn(exerciseDTO);

        // Act & Assert
        mockMvc.perform(get("/exercises/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Exercise 1"));
    }

    @Test
    public void testGetExerciseById_returnsNotFound_whenExerciseNotFound() throws Exception {
        // Arrange
        when(exerciseService.getExerciseById(99L))
                .thenThrow(new ResourceNotFoundException("Exercise with id 99 not found!"));

        // Act & Assert
        mockMvc.perform(get("/exercises/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Exercise with id 99 not found!"));
    }

    @Test
    public void testCreateExercise() throws Exception {
        // Arrange
        ExerciceDTO savedExercise =  new ExerciceDTO();
        savedExercise.setName("Exercise 1");
        String json = """
        {
            "name": "Exercise 1",
            "measureType": "REPETITION"
        }
        """;

        when(exerciseService.createExercise(any(Exercice.class)))
                .thenReturn(savedExercise);

        // Act & Assert
        mockMvc.perform(post("/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Exercise 1"));
    }

    @Test
    public void testUpdateExercise() throws Exception {
        // Arrange
        ExerciceDTO updatedExercise =  new ExerciceDTO();
        updatedExercise.setName("Exercise updated");
        String json = """
        {
            "name": "Exercise updated",
            "measureType": "REPETITION"
        }
        """;

        when(exerciseService.updateExercise(eq(1L),  any(Exercice.class)))
                .thenReturn(updatedExercise);

        // Act & Assert
        mockMvc.perform(put("/exercises/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Exercise updated"));
    }

    @Test
    public void testUpdateExercise_returnsNotFound_whenExerciseNotFound() throws Exception {
        // Arrange
        String json = """
        {
            "name": "Exercise updated",
            "measureType": "REPETITION"
        }
        """;

        when(exerciseService.updateExercise(eq(99L),  any(Exercice.class)))
                .thenThrow(new ResourceNotFoundException("Exercise with id 99 not found!"));

        // Act & Assert
        mockMvc.perform(put("/exercises/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Exercise with id 99 not found!"));
    }

    @Test
    public void testDeleteExercise() throws Exception {
        // Arrange
        doNothing().when(exerciseService).deleteExercise(eq(1L));

        // Act & Assert
        mockMvc.perform(delete("/exercises/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteExercise_returnsNotFound_whenExerciseNotFound() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Exercise with id 99 not found!")).when(exerciseService).deleteExercise(eq(99L));

        // Act & Assert
        mockMvc.perform(delete("/exercises/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Exercise with id 99 not found!"));
    }
}
