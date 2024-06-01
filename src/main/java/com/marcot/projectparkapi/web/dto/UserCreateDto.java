package com.marcot.projectparkapi.web.dto;

public record UserCreateDto(
        String username,
        String password
) {
    public UserCreateDto {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        if (password == null || !password.contains("@")) {
            throw new IllegalArgumentException("Invalid email address");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("The password must be at least 6 characters long.");
        }
    }
}