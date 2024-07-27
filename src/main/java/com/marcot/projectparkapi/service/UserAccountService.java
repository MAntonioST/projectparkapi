package com.marcot.projectparkapi.service;


import com.marcot.projectparkapi.entity.UserAccount;
import com.marcot.projectparkapi.exception.EntityNotFoundException;
import com.marcot.projectparkapi.exception.PasswordInvalidException;
import com.marcot.projectparkapi.exception.UsernameUniqueViolationException;
import com.marcot.projectparkapi.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RequiredArgsConstructor
@Service
public class UserAccountService {

        private final UserAccountRepository userRepository;
        private final PasswordEncoder passwordEncoder;

        @Transactional
        public UserAccount save(UserAccount userEntity) {
            try {
                userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
                return userRepository.save(userEntity);
            } catch (org.springframework.dao.DataIntegrityViolationException ex) {
                throw new UsernameUniqueViolationException(String.format("Username {%s} is already registered", userEntity.getUsername()));
            }
        }

        @Transactional(readOnly = true)
        public UserAccount getById(Long id) {
            return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("User id=%s not found!", id)));
        }

        @Transactional
        public UserAccount updatePassword(Long id, String currentPassword, String newPassword, String confirmPassword) {
            if(!newPassword.equals(confirmPassword)){
                throw new PasswordInvalidException("New password does not match the confirm password!");
            }
            UserAccount user = getById(id);
            if(!passwordEncoder.matches(currentPassword, user.getPassword())){
                throw new PasswordInvalidException("Your password does not match.");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            return user;
        }

        @Transactional(readOnly = true)
        public List<UserAccount> findAllUsers() {
            return userRepository.findAll();
        }

        @Transactional(readOnly = true)
        public UserAccount findByUsername(String username) {
            return  userRepository.findByUsername(username).orElseThrow(
                    () -> new EntityNotFoundException(String.format("User with {username} not found!", username))
            );
        }

        @Transactional(readOnly = true)
        public UserAccount.Role findRoleByUsername(String username) {
                return userRepository.findRoleByUsername(username);
        }
}
