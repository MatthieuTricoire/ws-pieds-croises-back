package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.dto.CourseDTO;
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
public class UserCourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final UserCourseRepository userCourseRepository;
    private final EmailService emailService;

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
                .filter(user_course -> user_course.getStatus() == UserCourse.Status.REGISTERED)
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
                .filter(user_course -> user_course.getUser().getId().equals(userId) && user_course.getStatus() == UserCourse.Status.REGISTERED)
                .findFirst()
                .orElseThrow(() -> new BusinessException("User not enrolled in this course"));

        // Supprimer la relation user-course
        course.getUserCourses().remove(userCourse);
        userCourse.getUser().getUserCourses().remove(userCourse);

        // Vérifie la liste d’attente et inscrit le premier
        course.getUserCourses().stream()
                .filter(user_course -> user_course.getStatus() == UserCourse.Status.WAITING_LIST)
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

        Long count = courseRepository.countUserCoursesInWeek(userId, startWeek, endWeek);

        if (count == null) {
            throw new ResourceNotFoundException("Aucun utilisateur trouvé avec l'id " + userId);
        }

        return count;
    }

}
