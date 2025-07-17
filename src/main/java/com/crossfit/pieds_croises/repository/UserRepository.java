package com.crossfit.pieds_croises.repository;

import com.crossfit.pieds_croises.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByRegistrationToken(String username);

    boolean existsByEmail(String email);

    Optional<User> findByResetPasswordToken(String token);
}
