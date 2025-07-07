package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.dto.UserDto;
import com.crossfit.pieds_croises.dto.UserUpdateDto;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.mapper.UserMapper;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found");
        }
        return users.stream()
                .map(userMapper::convertToDtoForAdmin)
                .toList();
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.convertToDtoForAdmin(user);
    }

    public UserDto createUser(UserDto userDto) {
        try {
            User user = userMapper.convertToEntity(userDto);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            User createdUser = userRepository.save(user);
            return userMapper.convertToCreatedDto(createdUser);
        } catch (Exception e) {
            throw new RuntimeException("Error creating user", e);
        }
    }

    public UserDto updateUser(Long id, UserUpdateDto userDto) {
        if (userDto.getId() != null && !userDto.getId().equals(id)) {
            throw new IllegalArgumentException("ID mismatch between path variable and request body");
        }

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        userMapper.updateUserFromDto(userDto, existingUser);
        existingUser.setUpdatedAt(LocalDateTime.now());

        try {
            User updatedUser = userRepository.save(existingUser);
            return userMapper.convertToDtoForAdmin(updatedUser);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user with id: " + id, e);
        }
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    public UserDto getMyProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.convertToDtoForUser(user);
    }

    public UserDto updateProfile(String username, UserUpdateDto userDto) {
        User existingUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username));

        userMapper.updateUserFromDto(userDto, existingUser);
        existingUser.setUpdatedAt(LocalDateTime.now());

        try {
            User updatedUser = userRepository.save(existingUser);
            return userMapper.convertToDtoForUser(updatedUser);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user: " + username, e);
        }
    }
}
