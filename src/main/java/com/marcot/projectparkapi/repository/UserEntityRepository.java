package com.marcot.projectparkapi.repository;

import com.marcot.projectparkapi.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    @Query("select u.role from UserEntity u where u.username like :username")
    UserEntity.Role findRoleByUsername(String username);
}
