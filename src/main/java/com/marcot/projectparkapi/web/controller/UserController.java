package com.marcot.projectparkapi.web.controller;

import com.marcot.projectparkapi.entity.User;
import com.marcot.projectparkapi.service.UserService;
import com.marcot.projectparkapi.web.dto.UserCreateDto;
import com.marcot.projectparkapi.web.dto.UserResponseDto;
import com.marcot.projectparkapi.web.dto.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> create(@RequestBody UserCreateDto createDto) {
        User user = userService.salvar(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toUserDto(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        User user = userService.buscarPorId(id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User entity) {
        User user = userService.updatePassword(id, entity.getPassword());
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

}