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

    @Query("SELECT COUNT(c) from Course c JOIN c.users u WHERE u.id = :userId AND c.startDatetime BETWEEN :startWeek AND :endWeek")
    Long countUserCoursesInWeek(@Param("userId") Long userId, @Param("startWeek") LocalDateTime startWeek, @Param("endWeek") LocalDateTime endWeek);

}
