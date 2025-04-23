package com.testing13.repository;

import com.testing13.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
        Optional<User> findByEmailIgnoreCase(String email);
}
