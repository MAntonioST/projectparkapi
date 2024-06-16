package com.marcot.projectparkapi.service;


import com.marcot.projectparkapi.entity.User;
import com.marcot.projectparkapi.exception.EntityNotFoundException;
import com.marcot.projectparkapi.exception.UsernameUniqueViolationException;
import com.marcot.projectparkapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class UserService {

        private final UserRepository userRepository;

        @Transactional
        public User salvar(User user) {
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                throw new UsernameUniqueViolationException(String.format("Username {%s} is already registered", user.getUsername()));
            }
            return userRepository.save(user);
        }

        @Transactional(readOnly = true)
        public User buscarPorId(Long id) {
            return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("User id=%s not found!", id)));
        }

        @Transactional
        public User updatePassword(Long id, String currentPassword, String newPassword, String confirmPassword) {
            if(!newPassword.equals(confirmPassword)){
                throw new RuntimeException("New password does not match the confirm password!");
            }
            Optional<User> optionalUser = userRepository.findById(id);
            User user = optionalUser.orElseThrow(() -> new RuntimeException("User not found!"));
            if(!user.getPassword().equals(currentPassword)){
                throw new RuntimeException("Your password does not match.");
            }
            user.setPassword(newPassword);
            return userRepository.save(user);
        }

        @Transactional(readOnly = true)
        public List<User> findAllUsers() {
            return userRepository.findAll();
        }
}
