package com.lingodrop.server.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lingodrop.server.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    
    Optional<User> findByUsername(String username);

}
