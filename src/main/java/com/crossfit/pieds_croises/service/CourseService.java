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
import com.crossfit.pieds_croises.model.UserCourse;
import com.crossfit.pieds_croises.repository.CourseRepository;
import com.crossfit.pieds_croises.repository.UserCourseRepository;
import com.crossfit.pieds_croises.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final UserRepository userRepository;
    private final UserCourseRepository userCourseRepository;
    private final EmailService emailService;

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
                .ifPresent(course -> {
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
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Vérifier si l'inscription existe déjà
        boolean alreadyRegistered = userCourseRepository.findByUserIdAndCourseId(userId, courseId).isPresent();
        if (alreadyRegistered) {
            throw new BusinessException("User already registered to this course");
        }

        if (user.isSuspended()) {
            throw new BusinessException("User is suspended");
        }

        // Déterminer le statut automatiquement selon le nombre d'inscrits
        long registeredCount = course.getUserCourses().stream()
                .filter(uc -> uc.getStatus() == UserCourse.Status.REGISTERED)
                .count();

        UserCourse.Status status = (registeredCount >= course.getPersonLimit())
                ? UserCourse.Status.WAITING_LIST
                : UserCourse.Status.REGISTERED;

        // Créer le lien UserCourse
        UserCourse userCourse = UserCourse.builder()
                .user(user)
                .course(course)
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();

        userCourseRepository.save(userCourse);

        course.getUserCourses().add(userCourse);
        user.getUserCourses().add(userCourse);

        course.changeStatus();
        courseRepository.save(course);

        return courseMapper.convertToDto(course);
    }

    public CourseDTO deleteUserFromCourse(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        // Vérifier que l'utilisateur est bien inscrit
        UserCourse userCourse = course.getUserCourses().stream()
                .filter(uc -> uc.getUser().getId().equals(userId) && uc.getStatus() == UserCourse.Status.REGISTERED)
                .findFirst()
                .orElseThrow(() -> new BusinessException("User not enrolled in this course"));

        // Supprimer la relation user-course
        course.getUserCourses().remove(userCourse);
        userCourse.getUser().getUserCourses().remove(userCourse);

        // Vérifie la liste d’attente et inscrit le premier
        course.getUserCourses().stream()
                .filter(uc -> uc.getStatus() == UserCourse.Status.WAITING_LIST)
                .sorted(Comparator.comparing(UserCourse::getCreatedAt)) // plus ancien en premier
                .findFirst()
                .ifPresent(firstWaiting -> {
                    firstWaiting.setStatus(UserCourse.Status.REGISTERED);

                    // Envoi de l’email de notification
                    Map<String, Object> variables = Map.of(
                            "firstname", firstWaiting.getUser().getFirstname(),
                            "lastname", firstWaiting.getUser().getLastname(),
                            "courseTitle", course.getTitle(),
                            "courseDate", course.getStartDatetime()
                    );
                    emailService.sendTemplateEmail(
                            firstWaiting.getUser().getEmail(),
                            "Bonne nouvelle, une place s’est libérée !",
                            "waiting-user-course-registration-notif", // ton template thymeleaf
                            variables
                    );
                });

        course.changeStatus();
        courseRepository.save(course);

        return courseMapper.convertToDto(course);
    }


    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        courseRepository.delete(course);
    }


    public List<UserDto> getUsersNotInCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        List<User> users = userRepository.findAllUsersNotInCourse(course, course.getCoach());
        return users.stream()
                .map(userMapper::convertToDtoForAdmin)
                .collect(Collectors.toList());
    }

    public Long getUserWeeklyCourseCount(Long userId, LocalDate weekDate) {
        // Calculate civil week
        LocalDate monday = weekDate.with(DayOfWeek.MONDAY);
        LocalDate sunday = monday.plusDays(6);

        LocalDateTime startWeek = monday.atStartOfDay();
        LocalDateTime endWeek = sunday.atTime(23, 59, 59);

        return courseRepository.countUserCoursesInWeek(userId, startWeek, endWeek);

    }
}
