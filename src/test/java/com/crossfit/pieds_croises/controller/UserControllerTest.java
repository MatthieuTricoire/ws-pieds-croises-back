package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.UserDto;
import com.crossfit.pieds_croises.dto.UserUpdateDto;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.security.CustomUserDetailsService;
import com.crossfit.pieds_croises.security.JwtAuthenticationFilter;
import com.crossfit.pieds_croises.security.JwtService;
import com.crossfit.pieds_croises.service.UserService;
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

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    public void testGetAllUsers() throws Exception {
        // Arrange
        UserDto user1 = new UserDto();
        user1.setFirstname("John");
        UserDto user2 = new UserDto();
        user2.setFirstname("William");

        when(userService.getAllUsers())
                .thenReturn(List.of(user1, user2));

        // Act & Assert
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstname").value("John"))
                .andExpect(jsonPath("$[1].firstname").value("William"));
    }

    @Test
    public void testGetUserById() throws Exception {
        // Arrange
        UserDto user = new UserDto();
        user.setFirstname("John");

        when(userService.getUserById(2L))
                .thenReturn(user);

        // Act & Assert
        mockMvc.perform(get("/users/{id}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("John"));
    }

    @Test
    public void testGetUserById_returnsNotFound_whenUserNotFound() throws Exception {
        // Arrange
        when(userService.getUserById(2L))
                .thenThrow(new ResourceNotFoundException("User not found with id: 2"));

        // Act & Assert
        mockMvc.perform(get("/users/{id}", 2L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found with id: 2"));
    }

    @Test
    public void testGetMyProfile() throws Exception {
        // Arrange
        UserDto user = new UserDto();
        user.setId(1L);
        User mockUser = new User();
        mockUser.setId(1L);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                mockUser,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userService.getMyProfile(1L))
                .thenReturn(user);

        // Act & Assert
        mockMvc.perform(get("/users/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void testGetMyProfile_returnsNotFound_whenUserNotFound() throws Exception {
        // Arrange
        User mockUser = new User();
        mockUser.setId(1L);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                mockUser,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userService.getMyProfile(1L))
                .thenThrow(new ResourceNotFoundException("User not found with id: 1"));

        // Act & Assert
        mockMvc.perform(get("/users/profile"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found with id: 1"));
    }

    @Test
    public void testCreateUser() throws Exception {
        // Arrange
        UserDto user = new UserDto();
        user.setFirstname("John");
        String json = """
        {
            "firstname": "John",
            "lastname": "Doe",
            "email": "john.doe@example.com",
            "phone": "0123456789"
        }
        """;

        when(userService.createUser(any(UserDto.class)))
                .thenReturn(user);

        // Act & Assert
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("John"));
    }

    @Test
    public void testCreateUser_returnsRuntime_whenCreationError() throws Exception {
        // Arrange
        String json = """
        {
            "firstname": "John",
            "lastname": "Doe",
            "email": "john.doe@example.com",
            "phone": "0123456789"
        }
        """;

        when(userService.createUser(any(UserDto.class)))
                .thenThrow(new RuntimeException("Error creating user"));

        // Act & Assert
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error creating user"));
    }

    @Test
    public void testUpdateUser() throws Exception {
        // Arrange
        UserDto updatedUser = new UserDto();
        updatedUser.setFirstname("John");
        String json = """
        {
            "firstname": "John",
            "lastname": "Doe",
            "email": "john.doe@example.com",
            "phone": "0123456789"
        }
        """;

        when(userService.updateUser(eq(1L), any(UserUpdateDto.class)))
                .thenReturn(updatedUser);

        // Act & Assert
        mockMvc.perform(put("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("John"));
    }

    @Test
    public void testUpdateUser_returnsBadRequest_whenIncorrectField() throws Exception {
        // Arrange
        String json = "{ \"email\": \"badEmail\" }";

        // Act & Assert
        mockMvc.perform(put("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists());
    }

    @Test
    public void testUpdateUser_returnsNotFound_whenUserNotFound() throws Exception {
        // Arrange
        String json = "{}";

        when(userService.updateUser(eq(1L), any(UserUpdateDto.class)))
                .thenThrow(new ResourceNotFoundException("User not found with id: 1"));

        // Act & Assert
        mockMvc.perform(put("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found with id: 1"));
    }

    @Test
    public void testUpdateUser_returnsIllegalArgument_whenIdMismatch() throws Exception {
        // Arrange
        String json = "{}";

        when(userService.updateUser(eq(1L), any(UserUpdateDto.class)))
                .thenThrow(new IllegalArgumentException("ID mismatch between path variable and request body"));

        // Act & Assert
        mockMvc.perform(put("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("ID mismatch between path variable and request body"));
    }

    @Test
    public void testUpdateUser_returnsRuntimeException_whenUpdateFailed() throws Exception {
        // Arrange
        String json = "{}";

        when(userService.updateUser(eq(1L), any(UserUpdateDto.class)))
                .thenThrow(new RuntimeException("Failed to update user with id: 1"));

        // Act & Assert
        mockMvc.perform(put("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to update user with id: 1"));
    }

    @Test
    public void testUpdateProfile() throws Exception {
        // Arrange
        UserDto updatedUser = new UserDto();
        updatedUser.setEmail("john.doe@example.com");
        String json = "{}";
        User mockUser = new User();
        mockUser.setEmail("john.doe@example.com");
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                mockUser,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userService.updateProfile(any(String.class), any(UserUpdateDto.class)))
                .thenReturn(updatedUser);

        // Act & Assert
        mockMvc.perform(put("/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    public void testUpdateProfile_returnsBadRequest_whenIncorrectField() throws Exception {
        // Arrange
        String json = "{ \"email\": \"badEmail\" }";

        // Act & Assert
        mockMvc.perform(put("/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists());
    }

    @Test
    public void testUpdateProfile_returnsNotFound_whenUserNotFound() throws Exception {
        // Arrange
        String json = "{}";
        User mockUser = new User();
        mockUser.setEmail("john.doe@example.com");
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                mockUser,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userService.updateProfile(any(String.class), any(UserUpdateDto.class)))
                .thenThrow(new ResourceNotFoundException("User not found with email: john.doe@example.com"));

        // Act & Assert
        mockMvc.perform(put("/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found with email: john.doe@example.com"));
    }

    @Test
    public void testUpdateProfile_returnsRuntimeException_whenUpdateFailed() throws Exception {
        // Arrange
        String json = "{}";
        User mockUser = new User();
        mockUser.setEmail("john.doe@example.com");
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                mockUser,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userService.updateProfile(any(String.class), any(UserUpdateDto.class)))
                .thenThrow(new RuntimeException("Failed to update user: john.doe@example.com"));

        // Act & Assert
        mockMvc.perform(put("/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to update user: john.doe@example.com"));
    }

    @Test
    public void testDeleteUser1() throws Exception {
        // Arrange
        doNothing().when(userService).deleteUser(1L);

        // Act & Assert
        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteUser1_returnsNotFound_whenUserNotFound() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("User not found with id: 1"))
                .when(userService).deleteUser(1L);

        // Act & Assert
        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found with id: 1"));
    }

    @Test
    public void testDeleteUser2() throws Exception {
        // Arrange
        User mockUser = new User();
        mockUser.setId(1L);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                mockUser,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        doNothing().when(userService).deleteUser(1L);

        // Act & Assert
        mockMvc.perform(delete("/users/profile"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteUser2_returnsNotFound_whenUserNotFound() throws Exception {
        // Arrange
        User mockUser = new User();
        mockUser.setId(1L);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                mockUser,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        doThrow(new ResourceNotFoundException("User not found with id: 1"))
                .when(userService).deleteUser(1L);

        // Act & Assert
        mockMvc.perform(delete("/users/profile"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found with id: 1"));
    }
}
