package com.crossfit.pieds_croises.repository;

import com.crossfit.pieds_croises.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
