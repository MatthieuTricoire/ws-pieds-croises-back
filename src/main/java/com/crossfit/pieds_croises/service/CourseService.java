package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.dto.CourseCreateDTO;
import com.crossfit.pieds_croises.dto.CourseDTO;
import com.crossfit.pieds_croises.exception.BusinessException;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.mapper.CourseMapper;
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

    private final LocalDateTime today = LocalDateTime.now();

    public List<CourseDTO> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        if (courses.isEmpty()) {
            throw new ResourceNotFoundException("The course list is empty");
        }
        return courses.stream().map(courseMapper::convertToDto).collect(Collectors.toList());
    }

    public List<CourseDTO> getCoursesNextTwoWeeks() {
        List<Course> courses = courseRepository.findByStartDatetimeBetweenOrderByStartDatetimeAsc(today, today.plusWeeks(2));
        if (courses.isEmpty()) {
            throw new ResourceNotFoundException("The course list is empty");
        }
        return courses.stream().map(courseMapper::convertToDto).collect(Collectors.toList());
    }

    public List<CourseDTO> getCoursesByDay(LocalDate date) {

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<Course> courses = courseRepository.findByStartDatetimeBetweenOrderByStartDatetimeAsc(startOfDay, endOfDay);
        if (courses.isEmpty()) {
            throw new ResourceNotFoundException("There are no courses today");
        }
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
                    throw new ResourceNotFoundException("A course already exists with this coach at this start date.");
                });

        Course course = courseMapper.convertToEntity(courseCreateDTO);

        User coach = userRepository.findById(courseCreateDTO.getCoachId())
                .orElseThrow(() -> new ResourceNotFoundException("Coach not found with id : " + courseCreateDTO.getCoachId()));

        if (!coach.isCoach()) {
            throw new ResourceNotFoundException("The selected user does not have the role of coach");
        }

        course.setCoach(coach);

        course = courseRepository.save(course);
        return courseMapper.convertToDto(course);
    }

    public CourseDTO updateCourse(Long id, @Valid CourseDTO courseDTO) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        User coach = userRepository.findById(courseDTO.getCoachId())
                .orElseThrow(() -> new ResourceNotFoundException("Coach not found with id : " + courseDTO.getCoachId()));

        if (!coach.isCoach()) {
            throw new ResourceNotFoundException("The selected user does not have the role of coach");
        }

        courseMapper.updateFromDTO(courseDTO, existingCourse);

        Course savedCourse = courseRepository.save(existingCourse);
        return courseMapper.convertToDto(savedCourse);
    }

    public void addUserToCourse(Long courseId, Long userId) {

        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not existing"));

        boolean userAlreadyRegisted = existingCourse.getUsers().stream().anyMatch(user -> user.getId().equals(existingUser.getId()));
        boolean courseFull = existingCourse.getStatus().equals(Course.Status.FULL);
        boolean userSuspended = existingUser.isSuspended();

        if (userAlreadyRegisted) {
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

    }

    public void deleteUserFromCourse(Long courseId, Long userId) {

        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not existing"));

        boolean userRegisted = existingCourse.getUsers().contains(existingUser);

        if (!userRegisted) {
            throw new BusinessException("User not enrolled in this course");
        }

        existingCourse.getUsers().remove(existingUser);
        existingUser.getCourses().remove(existingCourse);
        existingCourse.changeStatus();

        userRepository.save(existingUser);
        courseRepository.save(existingCourse);
    }

    public void deleteCourse(Long id) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        for (User user : existingCourse.getUsers()) {
            user.getCourses().remove(existingCourse);
            userRepository.save(user);
        }

        if (!existingCourse.getUsers().isEmpty()) {
            existingCourse.setStatus(Course.Status.CANCELLED);
            existingCourse.setUpdatedAt(LocalDateTime.now());
            courseRepository.save(existingCourse);
        }
        courseRepository.delete(existingCourse);

    }
}
