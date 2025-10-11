package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.UserSubscriptionDto;
import com.crossfit.pieds_croises.exception.ForbiddenException;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.security.CustomUserDetailsService;
import com.crossfit.pieds_croises.security.JwtAuthenticationFilter;
import com.crossfit.pieds_croises.security.JwtService;
import com.crossfit.pieds_croises.service.UserSubscriptionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserSubscriptionController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class UserSubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserSubscriptionService userSubscriptionService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    public void testCreateUserSubscription() throws Exception {
        // Arrange
        UserSubscriptionDto createdUserSubscription = new UserSubscriptionDto();
        createdUserSubscription.setId(1L);
        String json = String.format("""
        {
            "startDate": "%s",
            "userId": 1,
            "subscriptionId": 1
        }
        """, LocalDateTime.now());

        when(userSubscriptionService.createUserSubscription(any(UserSubscriptionDto.class)))
                .thenReturn(createdUserSubscription);

        // Act & Assert
        mockMvc.perform(post("/user-subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void testCreateUserSubscription_returnsBadRequest_whenMissingFields() throws Exception {
        // Arrange
        String json = "{}";

        // Act & Assert
        mockMvc.perform(post("/user-subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateUserSubscription_returnsResourceNotFound_whenUserNotFound() throws Exception {
        // Arrange
        String json = String.format("""
        {
            "startDate": "%s",
            "userId": 1,
            "subscriptionId": 1
        }
        """, LocalDateTime.now());

        when(userSubscriptionService.createUserSubscription(any(UserSubscriptionDto.class)))
                .thenThrow(new ResourceNotFoundException("User not found"));

        // Act & Assert
        mockMvc.perform(post("/user-subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    @Test
    public void testCreateUserSubscription_returnsForbidden_whenStartDatePassed() throws Exception {
        // Arrange
        String json = String.format("""
        {
            "startDate": "%s",
            "userId": 1,
            "subscriptionId": 1
        }
        """, LocalDateTime.now().minusDays(1));

        when(userSubscriptionService.createUserSubscription(any(UserSubscriptionDto.class)))
                .thenThrow(new ForbiddenException("Start date cannot be in the past"));

        // Act & Assert
        mockMvc.perform(post("/user-subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Start date cannot be in the past"));
    }

    @Test
    public void testGetUserSubscription() throws Exception {
        // Arrange
        UserSubscriptionDto userSubscription1 = new UserSubscriptionDto();
        userSubscription1.setId(1L);
        UserSubscriptionDto userSubscription2 = new UserSubscriptionDto();
        userSubscription2.setId(2L);

        when(userSubscriptionService.getAllUserSubscriptionsByUserId(any(Long.class)))
                .thenReturn(List.of(userSubscription1, userSubscription2));

        // Act & Assert
        mockMvc.perform(get("/user-subscriptions/user/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    public void testGetUserSubscription_returnsResourceNotFound_whenUserNotFound() throws Exception {
        // Arrange
        when(userSubscriptionService.getAllUserSubscriptionsByUserId(any(Long.class)))
                .thenThrow(new ResourceNotFoundException("User not found"));

        // Act & Assert
        mockMvc.perform(get("/user-subscriptions/user/{userId}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    @Test
    public void testGetUserSubscriptionById() throws Exception {
        // Arrange
        UserSubscriptionDto userSubscription = new UserSubscriptionDto();
        userSubscription.setId(1L);

        when(userSubscriptionService.getUserSubscriptionById(any(Long.class)))
                .thenReturn(userSubscription);

        // Act & Assert
        mockMvc.perform(get("/user-subscriptions/{userSubscriptionId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void testGetUserSubscriptionById_returnsResourceNotFound_whenUserNotFound() throws Exception {
        // Arrange
        when(userSubscriptionService.getUserSubscriptionById(any(Long.class)))
                .thenThrow(new ResourceNotFoundException("User subscription not found"));

        // Act & Assert
        mockMvc.perform(get("/user-subscriptions/{userSubscriptionId}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User subscription not found"));
    }

    @Test
    public void testFreezeUserSubscription() throws Exception {
        // Arrange
        LocalDateTime freezeStartDate = LocalDateTime.now().plusDays(1);
        LocalDateTime freezeEndDate = freezeStartDate.plusDays(5);
        String json = String.format("""
        {
            "freezeStartDate": "%s",
            "freezeEndDate": "%s"
        }
        """, freezeStartDate, freezeEndDate);

        // Act & Assert
        mockMvc.perform(put("/user-subscriptions/{userSubscriptionId}/freeze", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
        verify(userSubscriptionService).freezeUserSubscription(1L, freezeStartDate, freezeEndDate);
    }

    @Test
    public void testFreezeUserSubscription_returnsResourceNotFound_whenUserNotFound() throws Exception {
        // Arrange
        LocalDateTime freezeStartDate = LocalDateTime.now().plusDays(1);
        LocalDateTime freezeEndDate = freezeStartDate.plusDays(5);
        String json = String.format("""
        {
            "freezeStartDate": "%s",
            "freezeEndDate": "%s"
        }
        """, freezeStartDate, freezeEndDate);

        doThrow(new ResourceNotFoundException("User not found"))
                .when(userSubscriptionService).freezeUserSubscription(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class));

        // Act & Assert
        mockMvc.perform(put("/user-subscriptions/{userSubscriptionId}/freeze", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    @Test
    public void testFreezeUserSubscription_returnsForbidden_whenFreezeStartDateIsAfterFreezeEndDate() throws Exception {
        // Arrange
        LocalDateTime freezeStartDate = LocalDateTime.now().plusDays(6);
        LocalDateTime freezeEndDate = freezeStartDate.minusDays(5);
        String json = String.format("""
        {
            "freezeStartDate": "%s",
            "freezeEndDate": "%s"
        }
        """, freezeStartDate, freezeEndDate);

        doThrow(new ForbiddenException("Freeze start date cannot be after freeze end date")).when(userSubscriptionService)
                .freezeUserSubscription(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class));

        // Act & Assert
        mockMvc.perform(put("/user-subscriptions/{userSubscriptionId}/freeze", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Freeze start date cannot be after freeze end date"));
    }

    @Test
    public void testDeleteUserSubscription() throws Exception {
        // Arrange
        doNothing().when(userSubscriptionService).deleteUserSubscription(any(Long.class));

        // Act & Assert
        mockMvc.perform(delete("/user-subscriptions/{userSubscriptionId}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteUserSubscription_returnsResourceNotFound_whenUserNotFound() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("User subscription not found"))
                .when(userSubscriptionService).deleteUserSubscription(any(Long.class));

        // Act & Assert
        mockMvc.perform(delete("/user-subscriptions/{userSubscriptionId}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User subscription not found"));
    }
}
