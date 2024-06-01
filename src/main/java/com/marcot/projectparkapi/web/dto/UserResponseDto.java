package com.marcot.projectparkapi.web.dto;

public record UserResponseDto(
        String username,
        String password,
        String role
) {

}