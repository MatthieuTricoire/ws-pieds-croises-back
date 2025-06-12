package com.crossfit.pieds_croises.repository;

import com.crossfit.pieds_croises.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByStartDatetimeBetweenOrderByStartDatetimeAsc(LocalDateTime start, LocalDateTime end);

    Optional<Course> findByCoachIdAndStartDatetime(Long coachId, LocalDateTime startDatetime);
}
