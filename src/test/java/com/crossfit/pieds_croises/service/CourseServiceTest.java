package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.datetime.DateTimeProvider;
import com.crossfit.pieds_croises.dto.CourseCreateDTO;
import com.crossfit.pieds_croises.dto.CourseDTO;
import com.crossfit.pieds_croises.dto.CourseUpdateDTO;
import com.crossfit.pieds_croises.dto.UserDto;
import com.crossfit.pieds_croises.exception.BusinessException;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.mapper.CourseMapper;
import com.crossfit.pieds_croises.mapper.UserMapper;
import com.crossfit.pieds_croises.model.Course;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.repository.CourseRepository;
import com.crossfit.pieds_croises.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseMapper courseMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private DateTimeProvider dateTimeProvider;

    @InjectMocks
    private CourseService courseService;

    @Test
    public void testGetAllCourses() {
        // Arrange
        Course course1 = new Course();
        Course course2 = new Course();
        CourseDTO courseDTO1 = new CourseDTO();
        CourseDTO courseDTO2 = new CourseDTO();
        List<Course> courses = List.of(course1, course2);

        when(courseRepository.findAll()).thenReturn(courses);
        when(courseMapper.convertToDto(course1)).thenReturn(courseDTO1);
        when(courseMapper.convertToDto(course2)).thenReturn(courseDTO2);

        // Act
        List<CourseDTO> result = courseService.getAllCourses();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(courseDTO1, courseDTO2);
        verify(courseRepository, times(1)).findAll();
        verify(courseMapper, times(1)).convertToDto(course1);
        verify(courseMapper, times(1)).convertToDto(course2);
    }

    @Test
    public void testGetCoursesNextTwoWeeks() {
        // Arrange
        LocalDateTime now = LocalDateTime.of(2023, 10, 1, 12, 0);
        LocalDateTime twoWeeksLater = now.plusWeeks(2);

        Course course1 = new Course();
        course1.setStartDatetime(now.plusDays(1));

        Course course2 = new Course();
        course2.setStartDatetime(now.plusDays(3));

        List<Course> courses = Arrays.asList(course1, course2);

        when(dateTimeProvider.now()).thenReturn(now);
        when(courseRepository.findByStartDatetimeBetweenOrderByStartDatetimeAsc(now, twoWeeksLater)).thenReturn(courses);

        CourseDTO courseDTO1 = new CourseDTO();
        CourseDTO courseDTO2 = new CourseDTO();

        when(courseMapper.convertToDto(course1)).thenReturn(courseDTO1);
        when(courseMapper.convertToDto(course2)).thenReturn(courseDTO2);

        // Act
        List<CourseDTO> result = courseService.getCoursesNextTwoWeeks();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        verify(courseRepository, times(1)).findByStartDatetimeBetweenOrderByStartDatetimeAsc(now, twoWeeksLater);
    }

    @Test
    public void testGetCoursesByDay() {
        // Arrange
        LocalDate date = LocalDate.of(2023, 10, 1);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        Course course1 = new Course();
        course1.setStartDatetime(startOfDay.plusHours(10));

        Course course2 = new Course();
        course2.setStartDatetime(startOfDay.plusHours(14));

        List<Course> courses = Arrays.asList(course1, course2);

        when(courseRepository.findByStartDatetimeBetweenOrderByStartDatetimeAsc(startOfDay, endOfDay))
                .thenReturn(courses);

        CourseDTO dto1 = new CourseDTO();
        CourseDTO dto2 = new CourseDTO();

        when(courseMapper.convertToDto(course1)).thenReturn(dto1);
        when(courseMapper.convertToDto(course2)).thenReturn(dto2);

        // Act
        List<CourseDTO> result = courseService.getCoursesByDay(date);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(dto1, dto2);

        verify(courseRepository, times(1)).findByStartDatetimeBetweenOrderByStartDatetimeAsc(startOfDay, endOfDay);
        verify(courseMapper, times(1)).convertToDto(course1);
        verify(courseMapper, times(1)).convertToDto(course2);
    }

    @Test
    public void testGetCourseById() {
        // Arrange
        Long id = 1L;
        Course course = new Course();
        course.setId(id);

        CourseDTO courseDTO = new CourseDTO();

        when(courseRepository.findById(id)).thenReturn(Optional.of(course));
        when(courseMapper.convertToDto(course)).thenReturn(courseDTO);

        // Act
        CourseDTO result = courseService.getCourseByID(id);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(courseDTO);

        verify(courseRepository, times(1)).findById(id);
        verify(courseMapper, times(1)).convertToDto(course);
    }

    @Test
    public void testGetCourseById_WhenCourseNotFound_ShouldThrowException() {
        // Arrange
        Long id = 1L;
        when(courseRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> courseService.getCourseByID(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Course with id " + id + " not found");
        verify(courseRepository, times(1)).findById(id);
        verifyNoInteractions(courseMapper);
    }

    @Test
    public void testCreateCourse() {
        // Arrange
        CourseCreateDTO createDTO = new CourseCreateDTO();
        createDTO.setCoachId(1L);
        LocalDateTime startDateTime = LocalDateTime.of(2025, 7, 15, 10, 0);
        createDTO.setStartDatetime(startDateTime);

        when(courseRepository.findByCoachIdAndStartDatetime(1L, startDateTime))
                .thenReturn(Optional.empty());

        Course courseEntity = new Course();
        CourseDTO courseDTO = new CourseDTO();
        User coach = new User();
        coach.setId(1L);
        coach.getRoles().add("ROLE_COACH");

        when(courseMapper.convertToEntity(createDTO)).thenReturn(courseEntity);
        when(userRepository.findById(1L)).thenReturn(Optional.of(coach));

        LocalDateTime now = LocalDateTime.now();
        when(dateTimeProvider.now()).thenReturn(now);

        Course savedCourse = new Course();
        when(courseRepository.save(courseEntity)).thenReturn(savedCourse);
        when(courseMapper.convertToDto(savedCourse)).thenReturn(courseDTO);

        // Act
        CourseDTO result = courseService.createCourse(createDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(courseDTO);
        verify(courseRepository, times(1)).findByCoachIdAndStartDatetime(1L, startDateTime);
        verify(courseMapper, times(1)).convertToEntity(createDTO);
        verify(userRepository, times(1)).findById(1L);
        verify(dateTimeProvider, times(1)).now();
        verify(courseRepository, times(1)).save(courseEntity);
        verify(courseMapper, times(1)).convertToDto(savedCourse);
    }

    @Test
    public void testCreateCourse_WhenCourseAlreadyExists_ShouldThrowException() {
        // Arrange
        CourseCreateDTO createDTO = new CourseCreateDTO();
        createDTO.setCoachId(1L);
        LocalDateTime startDateTime = LocalDateTime.of(2025, 7, 15, 10, 0);
        createDTO.setStartDatetime(startDateTime);

        when(courseRepository.findByCoachIdAndStartDatetime(1L, startDateTime))
                .thenReturn(Optional.of(new Course()));

        // Act & Assert
        assertThatThrownBy(() -> courseService.createCourse(createDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("A course already exists with this coach at this start date.");

        verify(courseRepository, times(1)).findByCoachIdAndStartDatetime(1L, startDateTime);
        verifyNoMoreInteractions(courseMapper, userRepository, dateTimeProvider, courseRepository);
    }

    @Test
    public void testCreateCourse_WhenCoachNotFound_ShouldThrowException() {
        // Arrange
        CourseCreateDTO createDTO = new CourseCreateDTO();
        createDTO.setCoachId(1L);
        LocalDateTime startDateTime = LocalDateTime.of(2025, 7, 15, 10, 0);
        createDTO.setStartDatetime(startDateTime);

        when(courseRepository.findByCoachIdAndStartDatetime(1L, startDateTime))
                .thenReturn(Optional.empty());

        when(courseMapper.convertToEntity(createDTO)).thenReturn(new Course());
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> courseService.createCourse(createDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Coach not found with id : " + createDTO.getCoachId());

        verify(courseRepository, times(1)).findByCoachIdAndStartDatetime(1L, startDateTime);
        verify(courseMapper, times(1)).convertToEntity(createDTO);
        verify(userRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(dateTimeProvider, courseRepository);
    }

    @Test
    public void testCreateCourse_WhenUserIsNotCoach_ShouldThrowException() {
        // Arrange
        CourseCreateDTO createDTO = new CourseCreateDTO();
        createDTO.setCoachId(1L);
        LocalDateTime startDateTime = LocalDateTime.of(2025, 7, 15, 10, 0);
        createDTO.setStartDatetime(startDateTime);

        when(courseRepository.findByCoachIdAndStartDatetime(1L, startDateTime))
                .thenReturn(Optional.empty());

        when(courseMapper.convertToEntity(createDTO)).thenReturn(new Course());

        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThatThrownBy(() -> courseService.createCourse(createDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("The selected user does not have the role of coach");

        verify(courseRepository, times(1)).findByCoachIdAndStartDatetime(1L, startDateTime);
        verify(courseMapper, times(1)).convertToEntity(createDTO);
        verify(userRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(dateTimeProvider, courseRepository);
    }

    @Test
    public void testUpdateCourse() {
        // Arrange
        Long courseId = 1L;
        CourseUpdateDTO updateDTO = new CourseUpdateDTO();
        updateDTO.setCoachId(2L);

        Course existingCourse = new Course();

        User coach = new User();
        coach.setId(2L);
        coach.getRoles().add("ROLE_COACH");

        Course savedCourse = new Course();
        CourseDTO expectedDTO = new CourseDTO();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(existingCourse));
        when(userRepository.findById(2L)).thenReturn(Optional.of(coach));

        doNothing().when(courseMapper).updateFromDTO(updateDTO, existingCourse);

        when(courseRepository.save(existingCourse)).thenReturn(savedCourse);
        when(courseMapper.convertToDto(savedCourse)).thenReturn(expectedDTO);

        // Act
        CourseDTO result = courseService.updateCourse(courseId, updateDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedDTO);
        verify(courseRepository, times(1)).findById(courseId);
        verify(userRepository, times(1)).findById(2L);
        verify(courseMapper, times(1)).updateFromDTO(updateDTO, existingCourse);
        verify(courseRepository, times(1)).save(existingCourse);
        verify(courseMapper, times(1)).convertToDto(savedCourse);
    }

    @Test
    public void testUpdateCourse_WhenCourseNotFound_ShouldThrowException() {
        // Arrange
        Long courseId = 1L;
        CourseUpdateDTO updateDTO = new CourseUpdateDTO();
        updateDTO.setCoachId(2L);

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> courseService.updateCourse(courseId, updateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Course not found");

        verify(courseRepository, times(1)).findById(courseId);
        verifyNoMoreInteractions(userRepository, courseMapper, courseRepository);
    }

    @Test
    public void testUpdateCourse_WhenCoachNotFound_ShouldThrowException() {
        // Arrange
        Long courseId = 1L;
        CourseUpdateDTO updateDTO = new CourseUpdateDTO();
        updateDTO.setCoachId(2L);

        Course existingCourse = new Course();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(existingCourse));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> courseService.updateCourse(courseId, updateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Coach not found");

        verify(courseRepository, times(1)).findById(courseId);
        verify(userRepository, times(1)).findById(2L);
        verifyNoMoreInteractions(courseMapper, courseRepository);
    }

    @Test
    public void testUpdateCourse_WhenUserIsNotCoach_ShouldThrowException() {
        // Arrange
        Long courseId = 1L;
        CourseUpdateDTO updateDTO = new CourseUpdateDTO();
        updateDTO.setCoachId(2L);

        Course existingCourse = new Course();

        User coach = new User(); // pas de rôle "ROLE_COACH"

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(existingCourse));
        when(userRepository.findById(2L)).thenReturn(Optional.of(coach));

        // Act & Assert
        assertThatThrownBy(() -> courseService.updateCourse(courseId, updateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("does not have the role of coach");

        verify(courseRepository, times(1)).findById(courseId);
        verify(userRepository, times(1)).findById(2L);
        verifyNoMoreInteractions(courseMapper, courseRepository);
    }

    @Test
    public void testAddUserToCourse() {
        // Arrange
        Long courseId = 1L;
        Long userId = 2L;

        Course course = new Course();
        course.setStatus(Course.Status.OPEN); // status pas FULL
        course.setUsers(new ArrayList<>());
        course.setPersonLimit(18);

        User user = new User();
        user.setId(userId);
        user.setCourses(new ArrayList<>());
        user.setSuspensionStartDate(null);
        user.setSuspensionEndDate(null);

        CourseDTO expectedDTO = new CourseDTO();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(courseMapper.convertToDto(course)).thenReturn(expectedDTO);

        // Act
        CourseDTO result = courseService.addUserToCourse(courseId, userId);

        // Assert
        assertThat(result).isNotNull().isEqualTo(expectedDTO);
        assertThat(course.getUsers()).contains(user);
        assertThat(user.getCourses()).contains(course);

        verify(courseRepository, times(1)).findById(courseId);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
        verify(courseRepository, times(1)).save(course);
        verify(courseMapper, times(1)).convertToDto(course);
    }

    @Test
    public void testAddUserToCourse_WhenCourseNotFound_ShouldThrowException() {
        // Arrange
        Long courseId = 1L;
        Long userId = 2L;

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> courseService.addUserToCourse(courseId, userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Course not found with id: " + courseId);

        verify(courseRepository, times(1)).findById(courseId);
        verifyNoMoreInteractions(userRepository, courseRepository, courseMapper);
    }

    @Test
    public void testAddUserToCourse_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        Long courseId = 1L;
        Long userId = 2L;

        Course course = new Course();
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> courseService.addUserToCourse(courseId, userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not existing");

        verify(courseRepository, times(1)).findById(courseId);
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(courseRepository, courseMapper);
    }

    @Test
    public void testAddUserToCourse_WhenUserAlreadyRegistered_ShouldThrowException() {
        // Arrange
        Long courseId = 1L;
        Long userId = 2L;

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setSuspensionStartDate(null);
        existingUser.setSuspensionEndDate(null);

        Course course = new Course();
        course.setStatus(Course.Status.OPEN);
        course.setUsers(new ArrayList<>(List.of(existingUser)));

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThatThrownBy(() -> courseService.addUserToCourse(courseId, userId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("User already registered");

        verify(courseRepository, times(1)).findById(courseId);
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository, courseRepository, courseMapper);
    }

    @Test
    public void testAddUserToCourse_WhenCourseIsFull_ShouldThrowException() {
        // Arrange
        Long courseId = 1L;
        Long userId = 2L;

        User user = new User();
        user.setId(userId);
        user.setSuspensionStartDate(null);
        user.setSuspensionEndDate(null);

        Course course = new Course();
        course.setStatus(Course.Status.FULL);
        course.setUsers(new ArrayList<>());

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThatThrownBy(() -> courseService.addUserToCourse(courseId, userId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Course is already full");

        verify(courseRepository, times(1)).findById(courseId);
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository, courseRepository, courseMapper);
    }

    @Test
    public void testAddUserToCourse_WhenUserIsSuspended_ShouldThrowException() {
        // Arrange
        Long courseId = 1L;
        Long userId = 2L;

        User user = new User();
        user.setId(userId);
        user.setSuspensionStartDate(LocalDate.now().minusDays(1));
        user.setSuspensionEndDate(LocalDate.now().plusDays(1));

        Course course = new Course();
        course.setStatus(Course.Status.OPEN);
        course.setUsers(new ArrayList<>());

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThatThrownBy(() -> courseService.addUserToCourse(courseId, userId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("User suspended");

        verify(courseRepository, times(1)).findById(courseId);
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository, courseRepository, courseMapper);
    }

    @Test
    void testDeleteUserFromCourse() {
        // Arrange
        Long courseId = 1L;
        Long userId = 2L;

        Course course = new Course();
        course.setId(courseId);
        course.setUsers(new ArrayList<>());
        course.setPersonLimit(18);
        User user = new User();
        user.setId(userId);
        user.setCourses(new ArrayList<>());
        User coach = new User();
        coach.setId(99L);
        coach.setFirstname("John");
        coach.setLastname("Doe");

        course.setCoach(coach);
        course.getUsers().add(user);
        user.getCourses().add(course);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(courseRepository.save(course)).thenReturn(course);
        when(userRepository.save(user)).thenReturn(user);

        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setCoachId(coach.getId());
        courseDTO.setCoachName(coach.getFirstname() + " " + coach.getLastname());
        courseDTO.setUsersId(course.getUsers().stream().map(User::getId).toList());

        when(courseMapper.convertToDto(course)).thenReturn(courseDTO);

        // Act
        CourseDTO result = courseService.deleteUserFromCourse(courseId, userId);

        // Assert
        assertThat(result).isEqualTo(courseDTO);
        assertThat(course.getUsers()).doesNotContain(user);
        assertThat(user.getCourses()).doesNotContain(course);

        verify(courseRepository, times(1)).findById(courseId);
        verify(userRepository, times(1)).findById(userId);
        verify(courseRepository, times(1)).save(course);
        verify(userRepository, times(1)).save(user);
        verify(courseMapper, times(1)).convertToDto(course);
    }

    @Test
    void testDeleteUserFromCourse_WhenCourseNotFound_ShouldThrow() {
        Long courseId = 1L;
        Long userId = 2L;

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.deleteUserFromCourse(courseId, userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Course not found with id: " + courseId);

        verify(courseRepository, times(1)).findById(courseId);
        verifyNoMoreInteractions(userRepository, courseRepository, courseMapper);
    }

    @Test
    void testDeleteUserFromCourse_WhenUserNotFound_ShouldThrow() {
        Long courseId = 1L;
        Long userId = 2L;

        Course course = new Course();
        course.setId(courseId);
        course.setUsers(new ArrayList<>());

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.deleteUserFromCourse(courseId, userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not existing");

        verify(courseRepository, times(1)).findById(courseId);
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(courseRepository, userRepository, courseMapper);
    }

    @Test
    void testDeleteUserFromCourse_WhenUserNotRegistered_ShouldThrow() {
        Long courseId = 1L;
        Long userId = 2L;

        Course course = new Course();
        course.setId(courseId);
        course.setUsers(new ArrayList<>());

        User user = new User();
        user.setId(userId);
        user.setCourses(new ArrayList<>());

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> courseService.deleteUserFromCourse(courseId, userId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("User not enrolled in this course");

        verify(courseRepository, times(1)).findById(courseId);
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(courseRepository, userRepository, courseMapper);
    }

    @Test
    void testGetUsersNotInCourse() {
        // Arrange
        Long courseId = 1L;

        User coach = new User();
        coach.setId(100L);

        User user1 = new User();
        user1.setId(3L);

        User user2 = new User();
        user2.setId(4L);

        Course course = new Course();
        course.setId(courseId);
        course.setCoach(coach);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(userRepository.findAllUsersNotInCourse(course, coach)).thenReturn(List.of(user1, user2));

        UserDto userDto1 = new UserDto();
        userDto1.setId(user1.getId());

        UserDto userDto2 = new UserDto();
        userDto2.setId(user2.getId());

        when(userMapper.convertToDtoForAdmin(user1)).thenReturn(userDto1);
        when(userMapper.convertToDtoForAdmin(user2)).thenReturn(userDto2);

        // Act
        List<UserDto> result = courseService.getUsersNotInCourse(courseId);

        // Assert
        assertThat(result).containsExactlyInAnyOrder(userDto1, userDto2);

        verify(courseRepository, times(1)).findById(courseId);
        verify(userRepository, times(1)).findAllUsersNotInCourse(course, coach);
        verify(userMapper, times(1)).convertToDtoForAdmin(user1);
        verify(userMapper, times(1)).convertToDtoForAdmin(user2);
    }

    @Test
    void testGetUsersNotInCourse_whenCourseNotFound_thenThrowResourceNotFoundException() {
        // Arrange
        Long courseId = 1L;
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> courseService.getUsersNotInCourse(courseId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Course not found with id");

        verify(courseRepository, times(1)).findById(courseId);
        verifyNoInteractions(userRepository, userMapper);
    }
}
