package com.marcot.projectparkapi.repository;

import com.marcot.projectparkapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
