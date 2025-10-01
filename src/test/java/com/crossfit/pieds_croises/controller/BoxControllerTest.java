package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.BoxInfoDTO;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.security.CustomUserDetailsService;
import com.crossfit.pieds_croises.security.JwtAuthenticationFilter;
import com.crossfit.pieds_croises.security.JwtService;
import com.crossfit.pieds_croises.service.BoxService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoxController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class BoxControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoxService boxService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    public void testUpdateBox() throws Exception{
        // Arrange
        BoxInfoDTO BoxInfoDTO = new BoxInfoDTO();
        BoxInfoDTO.setName("PiedsCroises");
        String json = """
       {
            "id": 1,
            "name": "PiedsCroises",
            "email": "box@example.com",
            "phoneNumber": "0123456789",
            "address": "1 example street",
            "city": "Example City",
            "zipcode": "00000",
            "schedule": "24h/24"
       }
       """;

        when(boxService.updateBox(any(BoxInfoDTO.class)))
                .thenReturn(BoxInfoDTO);

        // Act & Assert
        mockMvc.perform(put("/box/box-info",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("PiedsCroises"));
    }

    @Test
    public void testUpdateBox_returnsBadRequest_whenBoxNotFound() throws Exception{
        // Arrange
        String json = "{}";

        // Act & Assert
        mockMvc.perform(put("/box/box-info",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.address").exists())
                .andExpect(jsonPath("$.city").exists())
                .andExpect(jsonPath("$.zipcode").exists());
    }

    @Test
    public void testUpdateBox_returnsNotFound_whenBoxNotFound() throws Exception{
        // Arrange
        String json = """
       {
            "id": 99,
            "name": "PiedsCroises",
            "email": "box@example.com",
            "phoneNumber": "0123456789",
            "address": "1 example street",
            "city": "Example City",
            "zipcode": "00000",
            "schedule": "24h/24"
       }
       """;

        when(boxService.updateBox(any(BoxInfoDTO.class)))
                .thenThrow(new ResourceNotFoundException("Box not found with id: 99"));

        // Act & Assert
        mockMvc.perform(put("/box/box-info",99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Box not found with id: 99"));
    }
}
