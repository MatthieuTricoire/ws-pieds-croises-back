package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.datetime.DateTimeProvider;
import com.crossfit.pieds_croises.dto.CourseCreateDTO;
import com.crossfit.pieds_croises.dto.CourseDTO;
import com.crossfit.pieds_croises.dto.CourseUpdateDTO;
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
}
