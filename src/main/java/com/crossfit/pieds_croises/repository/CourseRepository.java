package com.crossfit.pieds_croises.repository;

import com.crossfit.pieds_croises.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByStartDatetimeBetweenOrderByStartDatetimeAsc(LocalDateTime start, LocalDateTime end);

    Optional<Course> findByCoachIdAndStartDatetime(Long coachId, LocalDateTime startDatetime);

    @Query("""
                SELECT COUNT(uc) 
                FROM UserCourse uc 
                WHERE uc.user.id = :userId 
                  AND uc.course.startDatetime BETWEEN :startWeek AND :endWeek
                  AND uc.status = com.crossfit.pieds_croises.model.UserCourse.Status.REGISTERED
            """)
    Long countUserCoursesInWeek(
            @Param("userId") Long userId,
            @Param("startWeek") LocalDateTime startWeek,
            @Param("endWeek") LocalDateTime endWeek
    );


}
