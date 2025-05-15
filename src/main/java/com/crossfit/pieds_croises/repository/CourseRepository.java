package com.crossfit.pieds_croises.repository;

import com.crossfit.pieds_croises.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
