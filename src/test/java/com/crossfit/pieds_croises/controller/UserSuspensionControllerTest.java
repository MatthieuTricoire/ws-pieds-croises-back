package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.security.CustomUserDetailsService;
import com.crossfit.pieds_croises.security.JwtAuthenticationFilter;
import com.crossfit.pieds_croises.security.JwtService;
import com.crossfit.pieds_croises.service.UserSuspensionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserSuspensionController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class UserSuspensionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserSuspensionService userSuspensionService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    public void testApplyStrike() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/users/{userId}/increase-strike", 1L))
                .andExpect(status().isOk());
        verify(userSuspensionService).applyStrike(1L);
    }

    @Test
    public void testApplyStrike_returnsRuntime_whenUserNotFound() throws Exception {
        // Arrange
        doThrow(new RuntimeException("User not found"))
                .when(userSuspensionService).applyStrike(any(Long.class));

        // Act & Assert
        mockMvc.perform(put("/users/{userId}/increase-strike", 1L))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("User not found"));
    }

    @Test
    public void testCheckAndResetStrikes() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/users/check-strikes"))
                .andExpect(status().isOk());
        verify(userSuspensionService).checkAndResetSuspensions();
    }

    @Test
    public void testRemoveStrike() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/users/{userId}/decrease-strike", 1L))
                .andExpect(status().isOk());
        verify(userSuspensionService).removeStrike(1L);
    }

    @Test
    public void testRemoveStrike_returnsRuntime_whenUserNotFound() throws Exception {
        // Arrange
        doThrow(new RuntimeException("User not found"))
                .when(userSuspensionService).removeStrike(any(Long.class));

        // Act & Assert
        mockMvc.perform(put("/users/{userId}/decrease-strike", 1L))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("User not found"));
    }
}
