package com.marcot.projectparkapi.service;


import com.marcot.projectparkapi.entity.User;
import com.marcot.projectparkapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@RequiredArgsConstructor
@Service
public class UserService {

        private final UserRepository userRepository;

        @Transactional
        public User salvar(User entity) {
            return userRepository.save(entity);
        }

        @Transactional(readOnly = true)
        public User buscarPorId(Long id) {
            return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found!")
            );
        }

    @Transactional
    public User updatePassword(Long id, String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("The password must be at least 6 characters long!");
        }
        Optional<User> optionalUser = userRepository.findById(id);
        User user = optionalUser.orElseThrow(() -> new RuntimeException("User not found!"));
        user.setPassword(password);
        return userRepository.save(user);
    }
}
