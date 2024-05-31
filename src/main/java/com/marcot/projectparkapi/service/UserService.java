package com.marcot.projectparkapi.service;


import com.marcot.projectparkapi.entity.User;
import com.marcot.projectparkapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
            return userRepository.findById(id).orElseThrow(
                    () -> new RuntimeException("Usuário não encontrado.")
            );
        }

 }
