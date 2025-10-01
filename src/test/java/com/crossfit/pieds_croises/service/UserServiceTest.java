package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.datetime.DateTimeProvider;
import com.crossfit.pieds_croises.dto.UserDto;
import com.crossfit.pieds_croises.dto.UserSubscriptionDto;
import com.crossfit.pieds_croises.dto.UserUpdateDto;
import com.crossfit.pieds_croises.exception.DuplicateResourceException;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.mapper.UserMapper;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DateTimeProvider dateTimeProvider;

    @Mock
    private EmailService emailService;

    @Mock
    private UserSubscriptionService userSubscriptionService;

    @InjectMocks
    private UserService userService;

    @Test
    public void testGetAllUsers_WithoutSubscription() {
        // Arrange
        User user1 = new User();
        User user2 = new User();
        UserDto userDto1 = new UserDto();
        UserDto userDto2 = new UserDto();
        List<User> users = List.of(user1, user2);

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.convertToDtoForAdmin(user1)).thenReturn(userDto1);
        when(userMapper.convertToDtoForAdmin(user2)).thenReturn(userDto2);

        // Act
        List<UserDto> result = userService.getAllUsers(false);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(userDto1, userDto2);
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).convertToDtoForAdmin(user1);
        verify(userMapper, times(1)).convertToDtoForAdmin(user2);
    }

    @Test
    public void testGetAllUsers_WithSubscriptions() {
        // Arrange
        User user1 = new User();
        User user2 = new User();
        UserDto userDto1 = new UserDto();
        UserDto userDto2 = new UserDto();
        List<User> users = List.of(user1, user2);

        when(userRepository.findAllWithUserSubscriptions()).thenReturn(users);
        when(userMapper.convertToDtoForAdminWithSubscriptions(user1)).thenReturn(userDto1);
        when(userMapper.convertToDtoForAdminWithSubscriptions(user2)).thenReturn(userDto2);

        // Act
        List<UserDto> result = userService.getAllUsers(true);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(userDto1, userDto2);
        verify(userRepository, times(1)).findAllWithUserSubscriptions();
        verify(userMapper, times(1)).convertToDtoForAdminWithSubscriptions(user1);
        verify(userMapper, times(1)).convertToDtoForAdminWithSubscriptions(user2);
    }

    @Test
    public void testGetUserById() {
        // Arrange
        Long id = 1L;
        User user = new User();
        UserDto expectedUserDto = new UserDto();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.convertToDtoForAdmin(user)).thenReturn(expectedUserDto);

        // Act
        UserDto result = userService.getUserById(id);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedUserDto);
        verify(userRepository, times(1)).findById(id);
        verify(userMapper, times(1)).convertToDtoForAdmin(user);
    }

    @Test
    public void testGetUserById_WhenUserNotFound_ShouldThrownException() {
        // Arrange
        Long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.getUserById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with id: " + id);
        verify(userRepository, times(1)).findById(id);
        verifyNoInteractions(userMapper);
    }

    @Test
    public void testCreateUser() {
        // Arrange
        UserDto inputUserDto = new UserDto();
        inputUserDto.setEmail("john.doe@example.com");
        inputUserDto.setRoles(Set.of("ROLE_ADMIN"));
        User user = new User();
        user.setEmail("john.doe@example.com");
        User savedUser = new User();
        savedUser.setId(1L);
        UserDto expectedUserDto = new UserDto();
        LocalDateTime now = LocalDateTime.of(2025, 7, 16, 12, 0);

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());
        when(userMapper.convertToEntity(inputUserDto)).thenReturn(user);
        when(dateTimeProvider.now()).thenReturn(now);
        when(emailService.generateInvitationLink(nullable(String.class), anyString(), nullable(String.class))).thenReturn("link");
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.convertToCreatedDto(savedUser)).thenReturn(expectedUserDto);

        ReflectionTestUtils.setField(userService, "registrationTokenExpirationDays", 7);

        // Act
        UserDto result = userService.createUser(inputUserDto);

        // Assert
        assertThat(result).isEqualTo(expectedUserDto);
        assertThat(user.getRoles()).containsExactly("ROLE_ADMIN");
        assertThat(user.getRegistrationToken()).isNotNull();
        assertThat(user.getRegistrationTokenExpiryDate()).isEqualTo(now.plusDays(7));
        assertThat(user.getIsFirstLoginComplete()).isFalse();
        verify(userRepository).findByEmail("john.doe@example.com");
        verify(userMapper).convertToEntity(inputUserDto);
        verify(userRepository).save(user);
        verify(userMapper).convertToCreatedDto(savedUser);
        verify(emailService).generateInvitationLink(nullable(String.class), anyString(), nullable(String.class));
        verify(emailService).sendTemplateEmail(anyString(), anyString(), anyString(), anyMap());
        verifyNoInteractions(userSubscriptionService);
    }

    @Test
    public void testCreateUser_WithSubscription() {
        // Arrange
        UserDto inputUserDto = new UserDto();
        inputUserDto.setEmail("john.doe@example.com");
        inputUserDto.setSubscriptionId(10L);
        User user = new User();
        User savedUser = new User();
        savedUser.setId(1L);
        UserDto expectedUserDto = new UserDto();
        LocalDateTime now = LocalDateTime.of(2025, 7, 16, 12, 0);

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());
        when(userMapper.convertToEntity(inputUserDto)).thenReturn(user);
        when(dateTimeProvider.now()).thenReturn(now);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.convertToCreatedDto(savedUser)).thenReturn(expectedUserDto);
        when(emailService.generateInvitationLink(nullable(String.class), anyString(), nullable(String.class))).thenReturn("link");

        // Act
        UserDto result = userService.createUser(inputUserDto);

        // Assert
        assertThat(result).isEqualTo(expectedUserDto);
        verify(userSubscriptionService).createUserSubscription(any(UserSubscriptionDto.class));
    }

    @Test
    public void testCreateUser_WhenEmailExists_ShouldThrowException() {
        // Arrange
        UserDto inputUserDto = new UserDto();
        inputUserDto.setEmail("duplicate@example.com");
        User existingUser = new User();

        when(userRepository.findByEmail("duplicate@example.com")).thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(inputUserDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("User already exists");
        verify(userRepository).findByEmail("duplicate@example.com");
        verifyNoInteractions(userMapper, emailService, userSubscriptionService);
    }

    @Test
    public void testCreateUser_WithSubscriptionFailure_ShouldThrowRuntimeException() {
        // Arrange
        UserDto inputUserDto = new UserDto();
        inputUserDto.setEmail("test@example.com");
        inputUserDto.setSubscriptionId(10L);
        User user = new User();
        user.setEmail("john.doe@example.com");
        user.setFirstname("John");
        User savedUser = new User();
        savedUser.setId(1L);
        LocalDateTime now = LocalDateTime.of(2025, 7, 16, 12, 0);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userMapper.convertToEntity(inputUserDto)).thenReturn(user);
        when(dateTimeProvider.now()).thenReturn(now);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(emailService.generateInvitationLink(nullable(String.class), anyString(), nullable(String.class))).thenReturn("link");
        doThrow(new RuntimeException("Subscription error"))
                .when(userSubscriptionService).createUserSubscription(any(UserSubscriptionDto.class));

        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(inputUserDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to create subscription");
        verify(userSubscriptionService).createUserSubscription(any(UserSubscriptionDto.class));
    }

    @Test
    public void testUpdateUser() {
        // Arrange
        Long userId = 1L;
        UserUpdateDto inputUserDto = new UserUpdateDto();
        inputUserDto.setId(userId);
        User existingUser = new User();
        User savedUser = new User();
        UserDto expectedUserDto = new UserDto();
        LocalDateTime now = LocalDateTime.of(2025, 7, 16, 12, 0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        doNothing().when(userMapper).updateUserFromDto(inputUserDto, existingUser);
        when(dateTimeProvider.now()).thenReturn(now);
        when(userRepository.save(existingUser)).thenReturn(savedUser);
        when(userMapper.convertToDtoForAdmin(savedUser)).thenReturn(expectedUserDto);

        // Act
        UserDto result = userService.updateUser(userId, inputUserDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedUserDto);
        assertThat(existingUser.getUpdatedAt()).isEqualTo(now);
        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).updateUserFromDto(inputUserDto, existingUser);
        verify(dateTimeProvider, times(1)).now();
        verify(userRepository, times(1)).save(existingUser);
        verify(userMapper, times(1)).convertToDtoForAdmin(savedUser);
    }

    @Test
    public void testUpdateUser_WhenUserDtoIdIsNull_ShouldSucceed() {
        // Arrange
        Long userId = 1L;
        UserUpdateDto inputUserDto = new UserUpdateDto();
        User existingUser = new User();
        User savedUser = new User();
        UserDto expectedUserDto = new UserDto();
        LocalDateTime now = LocalDateTime.of(2025, 7, 16, 12, 0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        doNothing().when(userMapper).updateUserFromDto(inputUserDto, existingUser);
        when(dateTimeProvider.now()).thenReturn(now);
        when(userRepository.save(existingUser)).thenReturn(savedUser);
        when(userMapper.convertToDtoForAdmin(savedUser)).thenReturn(expectedUserDto);

        // Act
        UserDto result = userService.updateUser(userId, inputUserDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedUserDto);
        assertThat(existingUser.getUpdatedAt()).isEqualTo(now);
        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).updateUserFromDto(inputUserDto, existingUser);
        verify(dateTimeProvider, times(1)).now();
        verify(userRepository, times(1)).save(existingUser);
        verify(userMapper, times(1)).convertToDtoForAdmin(savedUser);
    }

    @Test
    public void testUpdateUser_WhenUserIdsDoNotMatch_ShouldThrownException() {
        // Arrange
        Long userId = 1L;
        UserUpdateDto inputUserDto = new UserUpdateDto();
        inputUserDto.setId(99L);

        // Act & Assert
        assertThatThrownBy(() -> userService.updateUser(userId, inputUserDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ID mismatch between path variable and request body");
        verifyNoInteractions(userRepository);
        verifyNoInteractions(userMapper);
        verifyNoInteractions(dateTimeProvider);
    }

    @Test
    public void testUpdateUser_WhenExistingUserNotFound_ShouldThrownException() {
        // Arrange
        Long userId = 1L;
        UserUpdateDto inputUserDto = new UserUpdateDto();
        inputUserDto.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.updateUser(userId, inputUserDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with id: " + userId);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userMapper);
        verifyNoInteractions(dateTimeProvider);
    }

    @Test
    public void testUpdateUser_WhenFailure_ShouldThrownException() {
        // Arrange
        Long userId = 1L;
        UserUpdateDto inputUserDto = new UserUpdateDto();
        inputUserDto.setId(userId);
        User existingUser = new User();
        LocalDateTime now = LocalDateTime.of(2025, 7, 16, 12, 0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        doNothing().when(userMapper).updateUserFromDto(inputUserDto, existingUser);
        when(dateTimeProvider.now()).thenReturn(now);
        when(userRepository.save(existingUser)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> userService.updateUser(userId, inputUserDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to update user with id: ");
        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).updateUserFromDto(inputUserDto, existingUser);
        verify(dateTimeProvider, times(1)).now();
        verify(userRepository, times(1)).save(existingUser);
        verifyNoMoreInteractions(userMapper, userRepository, dateTimeProvider);
    }

    @Test
    public void testDeleteUser() {
        // Arrange
        Long userId = 1L;
        User user = new User();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void testDeleteUser_WhenUserNotFound_ShouldThrownException() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with id: " + userId);
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testGetMyProfile() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        UserDto expectedUserDto = new UserDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.convertToDtoForUser(user)).thenReturn(expectedUserDto);

        // Act
        UserDto result = userService.getMyProfile(userId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedUserDto);
        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).convertToDtoForUser(user);
    }

    @Test
    public void testGetMyProfile_WhenUserNotFound_ShouldThrownException() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.getMyProfile(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with id: " + userId);
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userMapper);
    }

    @Test
    public void testUpdateMyProfile() {
        // Arrange
        String email = "unitTest@unitTest.test";
        UserUpdateDto inputUserDto = new UserUpdateDto();
        UserDto expectedUserDto = new UserDto();
        User existingUser = new User();
        User savedUser = new User();
        LocalDateTime now = LocalDateTime.of(2025, 7, 16, 12, 0);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        doNothing().when(userMapper).updateUserFromDto(inputUserDto, existingUser);
        when(dateTimeProvider.now()).thenReturn(now);
        when(userRepository.save(existingUser)).thenReturn(savedUser);
        when(userMapper.convertToDtoForUser(savedUser)).thenReturn(expectedUserDto);

        // Act
        UserDto result = userService.updateProfile(email, inputUserDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedUserDto);
        assertThat(existingUser.getUpdatedAt()).isEqualTo(now);
        verify(userRepository, times(1)).findByEmail(email);
        verify(userMapper, times(1)).updateUserFromDto(inputUserDto, existingUser);
        verify(dateTimeProvider, times(1)).now();
        verify(userRepository, times(1)).save(existingUser);
        verify(userMapper, times(1)).convertToDtoForUser(savedUser);
    }

    @Test
    public void testUpdateMyProfile_WhenExistingUserNotFound_ShouldThrownException() {
        // Arrange
        String email = "unitTest@unitTest.test";
        UserUpdateDto inputUserDto = new UserUpdateDto();

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.updateProfile(email, inputUserDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with email: " + email);
        verify(userRepository, times(1)).findByEmail(email);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userMapper);
        verifyNoInteractions(dateTimeProvider);
    }

    @Test
    public void testUpdateMyProfile_WhenFailure_ShouldThrownException() {
        // Arrange
        String email = "unitTest@unitTest.test";
        UserUpdateDto inputUserDto = new UserUpdateDto();
        User existingUser = new User();
        LocalDateTime now = LocalDateTime.of(2025, 7, 16, 12, 0);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        doNothing().when(userMapper).updateUserFromDto(inputUserDto, existingUser);
        when(dateTimeProvider.now()).thenReturn(now);
        when(userRepository.save(existingUser)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> userService.updateProfile(email, inputUserDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to update user: ");
        verify(userRepository, times(1)).findByEmail(email);
        verify(userMapper, times(1)).updateUserFromDto(inputUserDto, existingUser);
        verify(dateTimeProvider, times(1)).now();
        verify(userRepository, times(1)).save(existingUser);
        verifyNoMoreInteractions(userRepository, dateTimeProvider, userMapper);
    }
}
