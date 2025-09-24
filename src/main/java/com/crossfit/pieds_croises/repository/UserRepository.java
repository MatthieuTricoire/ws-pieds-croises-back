package com.crossfit.pieds_croises.repository;

import com.crossfit.pieds_croises.model.Course;
import com.crossfit.pieds_croises.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByRegistrationToken(String username);

    boolean existsByEmail(String email);

    @Query("""
                SELECT u FROM User u
                WHERE NOT EXISTS (
                    SELECT 1 FROM UserCourse uc
                    WHERE uc.user = u AND uc.course = :course
                )
                AND u != :coach
            """)
    List<User> findAllUsersNotInCourse(@Param("course") Course course, @Param("coach") User coach);


    @Query("SELECT DISTINCT u FROM User u " + "LEFT JOIN FETCH u.userSubscriptions us " + " LEFT JOIN FETCH us.subscription s")
    List<User> findAllWithUserSubscriptions();

    Optional<User> findByResetPasswordToken(String token);
}
