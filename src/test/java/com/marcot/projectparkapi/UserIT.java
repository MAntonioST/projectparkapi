package com.marcot.projectparkapi;

import com.marcot.projectparkapi.web.dto.UserCreateDto;
import com.marcot.projectparkapi.web.dto.UserResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/users/users-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/users/users-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserIT {

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void createUser_withValidUsernameAndPassword_returnsCreatedUser201() {
        // Arrange
        UserCreateDto createDto = new UserCreateDto();
        createDto.setUsername("new.user@techcorp.com"); // Use a unique username for the test
        createDto.setPassword("123456");

        // Act
        UserResponseDto responseBody = webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(createDto))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponseDto.class)
                .returnResult().getResponseBody();

        // Assert
        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getUsername()).isEqualTo("new.user@techcorp.com");
        org.assertj.core.api.Assertions.assertThat(responseBody.getRole()).isEqualTo("CLIENTE");
    }

    @Test
    public void createUser_withInvalidUsername_returnsBadRequest422() {
        // Arrange
        UserCreateDto createDto = new UserCreateDto();
        createDto.setUsername("invalid-email"); // Invalid email format
        createDto.setPassword("123456");

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(createDto))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid data provided")
                .jsonPath("$.errors.username").isEqualTo("The email format is invalid.");
    }


    @Test
    public void createUser_withShortPassword_returnsBadRequest422() {
        // Arrange
        UserCreateDto createDto = new UserCreateDto();
        createDto.setUsername("valid.user@techcorp.com");
        createDto.setPassword("123"); // Password too short

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(createDto))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid data provided")
                .jsonPath("$.errors.password").isEqualTo("The password must be exactly 6 characters long");
    }

    @Test
    public void getUserById_withNonExistingId_returnsNotFound404() {
        // Arrange
        Long nonExistingId = 999L;

        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/users/{id}", nonExistingId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo(String.format("User id=%s not found!", nonExistingId) );
    }

    @Test
    public void createUser_withExistingUsername_returnsConflict409() {
        // Arrange
        UserCreateDto createDto = new UserCreateDto();
        createDto.setUsername("admin@techcorp.com"); // Username that already exists
        createDto.setPassword("123456");

        // Act
        WebTestClient.ResponseSpec responseSpec = webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(createDto))
                .exchange();

        // Assert
        responseSpec.expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.message").isEqualTo(String.format("Username {%s} is already registered", createDto.getUsername()));
    }
}