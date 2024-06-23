package com.marcot.projectparkapi.service;


import com.marcot.projectparkapi.entity.UserEntity;
import com.marcot.projectparkapi.exception.EntityNotFoundException;
import com.marcot.projectparkapi.exception.PasswordInvalidException;
import com.marcot.projectparkapi.exception.UsernameUniqueViolationException;
import com.marcot.projectparkapi.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RequiredArgsConstructor
@Service
public class UserService {

        private final UserEntityRepository userRepository;

        @Transactional
        public UserEntity salvar(UserEntity user) {
            try {
                return userRepository.save(user);
            } catch (org.springframework.dao.DataIntegrityViolationException ex) {
                throw new UsernameUniqueViolationException(String.format("Username {%s} is already registered", user.getUsername()));
            }
        }

        @Transactional(readOnly = true)
        public UserEntity buscarPorId(Long id) {
            return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("User id=%s not found!", id)));
        }

        @Transactional
        public UserEntity updatePassword(Long id, String currentPassword, String newPassword, String confirmPassword) {
            if(!newPassword.equals(confirmPassword)){
                throw new PasswordInvalidException("New password does not match the confirm password!");
            }
            UserEntity user = buscarPorId(id);
            if(!user.getPassword().equals(currentPassword)){
                throw new PasswordInvalidException("Your password does not match.");
            }
            user.setPassword(newPassword);
            return user;
        }

        @Transactional(readOnly = true)
        public List<UserEntity> findAllUsers() {
            return userRepository.findAll();
        }

        @Transactional(readOnly = true)
        public UserEntity findByUsername(String username) {
            return  userRepository.findByUsername(username).orElseThrow(
                    () -> new EntityNotFoundException(String.format("User with {username} not found!", username))
            );
        }

        @Transactional(readOnly = true)
        public UserEntity.Role findRoleByUsername(String username) {
                return userRepository.findRoleByUsername(username);
        }
}
