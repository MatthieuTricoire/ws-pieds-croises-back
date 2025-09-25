package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.PerformanceHistoryDTO;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.security.CustomUserDetailsService;
import com.crossfit.pieds_croises.security.JwtAuthenticationFilter;
import com.crossfit.pieds_croises.security.JwtService;
import com.crossfit.pieds_croises.service.PerformanceHistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PerformanceHistoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class PerformanceHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PerformanceHistoryService performanceHistoryService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    public void testGetAllPerformanceHistoryByUserId_withMonthsParam() throws Exception {
        // Arrange
        PerformanceHistoryDTO performanceHistory1 = new PerformanceHistoryDTO();
        performanceHistory1.setId(1L);
        PerformanceHistoryDTO performanceHistory2 = new PerformanceHistoryDTO();
        performanceHistory2.setId(2L);
        User mockUser = new User();
        mockUser.setId(1L);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                mockUser,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(performanceHistoryService.getAllPerformanceHistoryForLastXMonths(1L, 3))
                .thenReturn(List.of(performanceHistory1, performanceHistory2));

        // Act & Assert
        mockMvc.perform(get("/performance-histories")
                        .param("months", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    public void testGetAllPerformanceHistoryByUserId_withoutMonthsParam() throws Exception {
        // Arrange
        PerformanceHistoryDTO performanceHistory1 = new PerformanceHistoryDTO();
        performanceHistory1.setId(1L);
        PerformanceHistoryDTO performanceHistory2 = new PerformanceHistoryDTO();
        performanceHistory2.setId(2L);
        User mockUser = new User();
        mockUser.setId(1L);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                mockUser,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(performanceHistoryService.getAllPerformanceHistory(1L))
                .thenReturn(List.of(performanceHistory1, performanceHistory2));

        // Act & Assert
        mockMvc.perform(get("/performance-histories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    public void testCreatePerformanceHistory() throws Exception {
        // Arrange
        PerformanceHistoryDTO savedPerformanceHistory = new PerformanceHistoryDTO();
        savedPerformanceHistory.setId(1L);
        String json = String.format("""
        {
            "measuredValue": 50.5,
            "date": "%s",
            "exerciseId": 1
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

        when(performanceHistoryService.createPerformanceHistory(any(PerformanceHistoryDTO.class), eq(1L)))
                .thenReturn(savedPerformanceHistory);

        // Act & Assert
        mockMvc.perform(post("/performance-histories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void testCreatePerformanceHistory_returnsBadRequest_whenMissingFields() throws Exception {
        // Arrange
        String json = "{}";

        // Act & Assert
        mockMvc.perform(post("/performance-histories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.measuredValue").exists())
                .andExpect(jsonPath("$.date").exists())
                .andExpect(jsonPath("$.exerciseId").exists());
    }

    @Test
    public void testCreatePerformanceHistory_returnsResourceNotFound_whenExerciseNotFound() throws Exception {
        // Arrange
        String json = String.format("""
        {
            "measuredValue": 50.5,
            "date": "%s",
            "exerciseId": 1
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

        when(performanceHistoryService.createPerformanceHistory(any(PerformanceHistoryDTO.class), eq(1L)))
                .thenThrow(new ResourceNotFoundException(("Exercise with id 1 not found.")));

        // Act & Assert
        mockMvc.perform(post("/performance-histories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Exercise with id 1 not found."));
    }

    @Test
    public void testUpdatePerformanceHistory() throws Exception {
        // Arrange
        PerformanceHistoryDTO updatedPerformanceHistory = new PerformanceHistoryDTO();
        updatedPerformanceHistory.setId(1L);
        String json = String.format("""
        {
            "id": 1,
            "measuredValue": 50.5,
            "date": "%s",
            "exerciseId": 1
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

        when(performanceHistoryService.updatePerformanceHistory(eq(1L), any(PerformanceHistoryDTO.class), eq(1L)))
                .thenReturn(updatedPerformanceHistory);

        // Act & Assert
        mockMvc.perform(put("/performance-histories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void testUpdatePerformanceHistory_returnsBadRequest_whenMissingFields() throws Exception {
        // Arrange
        String json = "{}";

        // Act & Assert
        mockMvc.perform(put("/performance-histories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.measuredValue").exists())
                .andExpect(jsonPath("$.date").exists())
                .andExpect(jsonPath("$.exerciseId").exists());
    }

    @Test
    public void testUpdatePerformanceHistory_returnsResourceNotFound_whenExerciseNotFound() throws Exception {
        // Arrange
        String json = String.format("""
        {
            "id": 1,
            "measuredValue": 50.5,
            "date": "%s",
            "exerciseId": 1
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

        when(performanceHistoryService.updatePerformanceHistory(eq(1L), any(PerformanceHistoryDTO.class), eq(1L)))
                .thenThrow(new ResourceNotFoundException(("Exercise with id 1 not found.")));

        // Act & Assert
        mockMvc.perform(put("/performance-histories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Exercise with id 1 not found."));
    }

    @Test
    public void testUpdatePerformanceHistory_returnsAccessDenied_whenUserNotMatch() throws Exception {
        // Arrange
        String json = String.format("""
        {
            "id": 1,
            "measuredValue": 50.5,
            "date": "%s",
            "exerciseId": 1
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

        when(performanceHistoryService.updatePerformanceHistory(eq(1L), any(PerformanceHistoryDTO.class), eq(1L)))
                .thenThrow(new AccessDeniedException(("Not authorized to update this performanceHistory.")));

        // Act & Assert
        mockMvc.perform(put("/performance-histories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Not authorized to update this performanceHistory."));
    }

    @Test
    public void testDeletePerformanceHistory() throws Exception {
        // Arrange
        User mockUser = new User();
        mockUser.setId(1L);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                mockUser,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        doNothing().when(performanceHistoryService).deletePerformanceHistory(1L, 1L);

        // Act & Assert
        mockMvc.perform(delete("/performance-histories/{id}", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeletePerformanceHistory_returnsNotFound_whenPerformanceHistoryNotFound() throws Exception {
        // Arrange
        User mockUser = new User();
        mockUser.setId(1L);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                mockUser,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        doThrow(new ResourceNotFoundException("PerformanceHistory with id 1 not found."))
                .when(performanceHistoryService).deletePerformanceHistory(1L, 1L);

        // Act & Assert
        mockMvc.perform(delete("/performance-histories/{id}", 1))
                .andExpect(status().isNotFound())
                .andExpect(content().string("PerformanceHistory with id 1 not found."));
    }

    @Test
    public void testDeletePerformanceHistory_returnsAccessDenied_whenUserNotMatch() throws Exception {
        // Arrange
        User mockUser = new User();
        mockUser.setId(1L);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                mockUser,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        doThrow(new AccessDeniedException("Not authorized to delete this performanceHistory."))
                .when(performanceHistoryService).deletePerformanceHistory(1L, 1L);

        // Act & Assert
        mockMvc.perform(delete("/performance-histories/{id}", 1))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Not authorized to delete this performanceHistory."));
    }
}
