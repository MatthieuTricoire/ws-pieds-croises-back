package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.WeightHistoryDTO;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.security.CustomUserDetailsService;
import com.crossfit.pieds_croises.security.JwtAuthenticationFilter;
import com.crossfit.pieds_croises.security.JwtService;
import com.crossfit.pieds_croises.service.WeightHistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WeightHistoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class WeightHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeightHistoryService weightHistoryService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    public void testGetAllWeightHistoryByUserId_withMonthsParam() throws Exception {
        // Arrange
        WeightHistoryDTO weightHistory1 = new WeightHistoryDTO();
        weightHistory1.setWeight(60.00);
        WeightHistoryDTO weightHistory2 = new WeightHistoryDTO();
        weightHistory2.setWeight(62.00);
        User mockUser = new User();
        mockUser.setId(1L);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                mockUser,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(weightHistoryService.getAllWeightHistoryForLastXMonths(1L, 3))
                .thenReturn(List.of(weightHistory1, weightHistory2));

        // Act & Assert
        mockMvc.perform(get("/weight-histories")
                        .param("months", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].weight").value(60.00))
                .andExpect(jsonPath("$[1].weight").value(62.00));
    }

    @Test
    public void testGetAllWeightHistoryByUserId_withoutMonthsParam() throws Exception {
        // Arrange
        WeightHistoryDTO weightHistory1 = new WeightHistoryDTO();
        weightHistory1.setWeight(60.00);
        WeightHistoryDTO weightHistory2 = new WeightHistoryDTO();
        weightHistory2.setWeight(62.00);
        User mockUser = new User();
        mockUser.setId(1L);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                mockUser,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(weightHistoryService.getAllWeightHistory(1L))
                .thenReturn(List.of(weightHistory1, weightHistory2));

        // Act & Assert
        mockMvc.perform(get("/weight-histories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].weight").value(60.00))
                .andExpect(jsonPath("$[1].weight").value(62.00));
    }

    @Test
    public void testCreateWeightHistory() throws Exception {
        // Arrange
        WeightHistoryDTO savedWeightHistory = new WeightHistoryDTO();
        savedWeightHistory.setWeight(60.00);
        String json = String.format("""
        {
            "weight": 60.00,
            "date": "%s"
        }
        """, LocalDate.now());
        User mockUser = new User();
        mockUser.setId(1L);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                mockUser,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(weightHistoryService.createWeightHistory(any(WeightHistoryDTO.class), eq(1L)))
                .thenReturn(savedWeightHistory);

        // Act & Assert
        mockMvc.perform(post("/weight-histories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.weight").value(60.00));
    }

    @Test
    public void testCreateWeightHistory_returnsBadRequest_whenMissingFields() throws Exception {
        // Arrange
        String json = "{}";

        // Act & Assert
        mockMvc.perform(post("/weight-histories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.weight").exists())
                .andExpect(jsonPath("$.date").exists());
    }

    @Test
    public void testCreateWeightHistory_returnsNotFound_whenUserNotFound() throws Exception {
        // Arrange
        String json = String.format("""
        {
            "weight": 60.00,
            "date": "%s"
        }
        """, LocalDate.now());
        User mockUser = new User();
        mockUser.setId(1L);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                mockUser,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(weightHistoryService.createWeightHistory(any(WeightHistoryDTO.class), eq(1L)))
                .thenThrow(new ResourceNotFoundException("User with id 1 not found"));

        // Act & Assert
        mockMvc.perform(post("/weight-histories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User with id 1 not found"));
    }

    @Test
    public void testUpdateWeightHistory() throws Exception {
        // Arrange
        WeightHistoryDTO updatedWeightHistory = new WeightHistoryDTO();
        updatedWeightHistory.setWeight(60.00);
        String json = String.format("""
        {
            "id": 1,
            "weight": 60.00,
            "date": "%s"
        }
        """, LocalDate.now());
        User mockUser = new User();
        mockUser.setId(1L);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                mockUser,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(weightHistoryService.updateWeightHistory(eq(1L), any(WeightHistoryDTO.class), eq(1L)))
                .thenReturn(updatedWeightHistory);

        // Act & Assert
        mockMvc.perform(put("/weight-histories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weight").value(60.00));
    }

    @Test
    public void testUpdateWeightHistory_returnsBadRequest_whenMissingFields() throws Exception {
        // Arrange
        String json = "{}";

        // Act & Assert
        mockMvc.perform(put("/weight-histories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.weight").exists())
                .andExpect(jsonPath("$.date").exists());
    }

    @Test
    public void testUpdateWeightHistory_returnsNotFound_whenWeightHistoryNotFound() throws Exception {
        // Arrange
        String json = String.format("""
        {
            "id": 1,
            "weight": 60.00,
            "date": "%s"
        }
        """, LocalDate.now());
        User mockUser = new User();
        mockUser.setId(1L);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                mockUser,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(weightHistoryService.updateWeightHistory(eq(1L), any(WeightHistoryDTO.class), eq(1L)))
                .thenThrow(new ResourceNotFoundException("WeightHistory with id 1 not found"));

        // Act & Assert
        mockMvc.perform(put("/weight-histories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().string("WeightHistory with id 1 not found"));
    }

    @Test
    public void testUpdateWeightHistory_returnsAccessDenied_whenUserNotMatch() throws Exception {
        // Arrange
        String json = String.format("""
        {
            "id": 1,
            "weight": 60.00,
            "date": "%s"
        }
        """, LocalDate.now());
        User mockUser = new User();
        mockUser.setId(1L);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                mockUser,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(weightHistoryService.updateWeightHistory(eq(1L), any(WeightHistoryDTO.class), eq(1L)))
                .thenThrow(new AccessDeniedException("Not authorized to update this weightHistory."));

        // Act & Assert
        mockMvc.perform(put("/weight-histories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Not authorized to update this weightHistory."));
    }

    @Test
    public void testDeleteWeightHistory() throws Exception {
        // Arrange
        User mockUser = new User();
        mockUser.setId(1L);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                mockUser,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        doNothing().when(weightHistoryService).deleteWeightHistory(1L, 1L);

        // Act & Assert
        mockMvc.perform(delete("/weight-histories/{id}", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteWeightHistory_returnsNotFound_whenWeightHistoryNotFound() throws Exception {
        // Arrange
        User mockUser = new User();
        mockUser.setId(1L);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                mockUser,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        doThrow(new ResourceNotFoundException("WeightHistory with id 1 not found"))
                .when(weightHistoryService).deleteWeightHistory(1L, 1L);

        // Act & Assert
        mockMvc.perform(delete("/weight-histories/{id}", 1))
                .andExpect(status().isNotFound())
                .andExpect(content().string("WeightHistory with id 1 not found"));
    }

    @Test
    public void testDeleteWeightHistory_returnsAccessDenied_whenUserNotMatch() throws Exception {
        // Arrange
        User mockUser = new User();
        mockUser.setId(1L);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                mockUser,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        doThrow(new AccessDeniedException("Not authorized to delete this weightHistory."))
                .when(weightHistoryService).deleteWeightHistory(1L, 1L);

        // Act & Assert
        mockMvc.perform(delete("/weight-histories/{id}", 1))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Not authorized to delete this weightHistory."));
    }
}
