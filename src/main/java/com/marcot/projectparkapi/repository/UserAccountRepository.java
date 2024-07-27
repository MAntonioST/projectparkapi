package com.marcot.projectparkapi.repository;

import com.marcot.projectparkapi.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);

    @Query("select u.role from UserAccount u where u.username like :username")
    UserAccount.Role findRoleByUsername(String username);
}
