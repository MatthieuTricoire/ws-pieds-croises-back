package com.crossfit.pieds_croises.repository;

import com.crossfit.pieds_croises.model.UserCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {

    Optional<UserCourse> findByUserIdAndCourseId(Long userId, Long courseId);

}
