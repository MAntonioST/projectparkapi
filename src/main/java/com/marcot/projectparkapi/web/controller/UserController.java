package com.marcot.projectparkapi.web.controller;

import com.marcot.projectparkapi.entity.User;
import com.marcot.projectparkapi.service.UserService;
import com.marcot.projectparkapi.web.dto.UserCreateDto;
import com.marcot.projectparkapi.web.dto.UserPasswordDto;
import com.marcot.projectparkapi.web.dto.UserResponseDto;
import com.marcot.projectparkapi.web.dto.mapper.UserMapper;
import jakarta.validation.Valid;
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
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserCreateDto createDto) {
        User user = userService.salvar(UserMapper.toUser(createDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {
        User user = userService.buscarPorId(id);
        return ResponseEntity.ok(UserMapper.toDto(user));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id, @Valid @RequestBody UserPasswordDto dto) {
        User user = userService.updatePassword(id, dto.getCurrentPassword(), dto.getNewPassword(), dto.getConfirmPassword());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(UserMapper.toListDto(users));
    }

}