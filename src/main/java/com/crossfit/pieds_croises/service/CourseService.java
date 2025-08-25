package com.crossfit.pieds_croises.service;

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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public List<CourseDTO> getAllCourses() {
        List<Course> courses = courseRepository.findAll();

        return courses.stream().map(courseMapper::convertToDto).collect(Collectors.toList());
    }

    public List<CourseDTO> getCoursesNextTwoWeeks() {
        LocalDateTime now = LocalDateTime.now();
        List<Course> courses = courseRepository.findByStartDatetimeBetweenOrderByStartDatetimeAsc(now, now.plusWeeks(2));

        return courses.stream().map(courseMapper::convertToDto).collect(Collectors.toList());
    }

    public List<CourseDTO> getCoursesByDay(LocalDate date) {

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<Course> courses = courseRepository.findByStartDatetimeBetweenOrderByStartDatetimeAsc(startOfDay, endOfDay);

        return courses.stream().map(courseMapper::convertToDto).collect(Collectors.toList());
    }

    public CourseDTO getCourseByID(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course with id " + id + " not found"));
        return courseMapper.convertToDto(course);
    }

    public CourseDTO createCourse(@Valid CourseCreateDTO courseCreateDTO) {

        courseRepository.findByCoachIdAndStartDatetime(courseCreateDTO.getCoachId(), courseCreateDTO.getStartDatetime())
                .ifPresent(c -> {
                    throw new BusinessException("A course already exists with this coach at this start date.");
                });

        Course course = courseMapper.convertToEntity(courseCreateDTO);


        User coach = userRepository.findById(courseCreateDTO.getCoachId())
                .orElseThrow(() -> new ResourceNotFoundException("Coach not found with id : " + courseCreateDTO.getCoachId()));

        if (!coach.isCoach()) {
            throw new ResourceNotFoundException("The selected user does not have the role of coach");
        }

        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        course.setCoach(coach);

        course = courseRepository.save(course);
        return courseMapper.convertToDto(course);
    }

    public CourseDTO updateCourse(Long id, @Valid CourseUpdateDTO courseUpdateDTO) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        User coach = userRepository.findById(courseUpdateDTO.getCoachId())
                .orElseThrow(() -> new ResourceNotFoundException("Coach not found with id : " + courseUpdateDTO.getCoachId()));

        if (!coach.isCoach()) {
            throw new ResourceNotFoundException("The selected user does not have the role of coach");
        }

        courseMapper.updateFromDTO(courseUpdateDTO, existingCourse);

        existingCourse.setCoach(coach);

        Course savedCourse = courseRepository.save(existingCourse);
        return courseMapper.convertToDto(savedCourse);
    }

    public CourseDTO addUserToCourse(Long courseId, Long userId) {

        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not existing"));

        boolean userAlreadyRegistered = existingCourse.getUsers().stream().anyMatch(user -> user.getId().equals(existingUser.getId()));
        boolean courseFull = existingCourse.getStatus().equals(Course.Status.FULL);
        boolean userSuspended = existingUser.isSuspended();

        if (userAlreadyRegistered) {
            throw new BusinessException("User already registered to this course");
        }

        if (courseFull) {
            throw new BusinessException("Course is already full");
        }

        if (userSuspended) {
            throw new BusinessException("User suspended");
        }

        existingCourse.getUsers().add(existingUser);
        existingUser.getCourses().add(existingCourse);
        existingCourse.changeStatus();

        userRepository.save(existingUser);
        courseRepository.save(existingCourse);

        return courseMapper.convertToDto(existingCourse);
    }

    public CourseDTO deleteUserFromCourse(Long courseId, Long userId) {

        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not existing"));

        boolean userRegistered = existingCourse.getUsers().contains(existingUser);

        if (!userRegistered) {
            throw new BusinessException("User not enrolled in this course");
        }

        existingCourse.getUsers().remove(existingUser);
        existingUser.getCourses().remove(existingCourse);
        existingCourse.changeStatus();

        userRepository.save(existingUser);
        courseRepository.save(existingCourse);

        return courseMapper.convertToDto(existingCourse);
    }

    public void deleteCourse(Long id) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        for (User user : existingCourse.getUsers()) {
            user.getCourses().remove(existingCourse);
            userRepository.save(user);
        }

        courseRepository.delete(existingCourse);
    }

    public List<UserDto> getUsersNotInCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        List<User> users = userRepository.findAllUsersNotInCourse(course, course.getCoach());
        return users.stream()
                .map(userMapper::convertToDtoForAdmin)
                .collect(Collectors.toList());
    }
}
