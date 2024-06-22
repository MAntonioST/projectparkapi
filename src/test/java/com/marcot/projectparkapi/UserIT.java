package com.marcot.projectparkapi;

import com.marcot.projectparkapi.web.dto.UserCreateDto;
import com.marcot.projectparkapi.web.dto.UserResponseDto;
import com.marcot.projectparkapi.web.exception.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
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
    public void createUser_WhenValidUsernameAndPassword_returnsCreatedUser201() {
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
    public void createUser_WhenInvalidUsername_returnsErrroMessageStatus422() {

        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto("", "123456"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY) // Use HttpStatus para melhor legibilidade
                .expectBody(ErrorMessage.class)
                .consumeWith(result -> {
                    org.assertj.core.api.Assertions.assertThat(result.getResponseBody()).isNotNull();
                    org.assertj.core.api.Assertions.assertThat(result.getResponseBody().getStatus()).isEqualTo(422);
                });


        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto("david@", "123456"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY) // Use HttpStatus para melhor legibilidade
                .expectBody(ErrorMessage.class)
                .consumeWith(result -> {
                    org.assertj.core.api.Assertions.assertThat(result.getResponseBody()).isNotNull();
                    org.assertj.core.api.Assertions.assertThat(result.getResponseBody().getStatus()).isEqualTo(422);
                });

        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto("david@email", "123456"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY) // Use HttpStatus para melhor legibilidade
                .expectBody(ErrorMessage.class)
                .consumeWith(result -> {
                    org.assertj.core.api.Assertions.assertThat(result.getResponseBody()).isNotNull();
                    org.assertj.core.api.Assertions.assertThat(result.getResponseBody().getStatus()).isEqualTo(422);
                });

    }



    @Test
    public void createUser_WhenPasswordInvalid_returnsErrorMessageStatus422() {

            webTestClient.post()
                    .uri("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new UserCreateDto("david@gmail.com", ""))
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY) // Use HttpStatus para melhor legibilidade
                    .expectBody(ErrorMessage.class)
                    .consumeWith(result -> {
                        org.assertj.core.api.Assertions.assertThat(result.getResponseBody()).isNotNull();
                        org.assertj.core.api.Assertions.assertThat(result.getResponseBody().getStatus()).isEqualTo(422);
                    });


            webTestClient.post()
                    .uri("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new UserCreateDto("david@gmail.com", "123"))
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY) // Use HttpStatus para melhor legibilidade
                    .expectBody(ErrorMessage.class)
                    .consumeWith(result -> {
                        org.assertj.core.api.Assertions.assertThat(result.getResponseBody()).isNotNull();
                        org.assertj.core.api.Assertions.assertThat(result.getResponseBody().getStatus()).isEqualTo(422);
                    });

            webTestClient.post()
                    .uri("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new UserCreateDto("david@gmail.com", "1234567"))
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY) // Use HttpStatus para melhor legibilidade
                    .expectBody(ErrorMessage.class)
                    .consumeWith(result -> {
                        org.assertj.core.api.Assertions.assertThat(result.getResponseBody()).isNotNull();
                        org.assertj.core.api.Assertions.assertThat(result.getResponseBody().getStatus()).isEqualTo(422);
                    });

        }

    @Test
    public void createUser_WhenDuplicateUsername_returns409Conflict() {

        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto("admin@techcorp.com", "admin1"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT) // Use HttpStatus para melhor legibilidade
                .expectBody(ErrorMessage.class)
                .consumeWith(result -> {
                    org.assertj.core.api.Assertions.assertThat(result.getResponseBody()).isNotNull();
                    org.assertj.core.api.Assertions.assertThat(result.getResponseBody().getStatus()).isEqualTo(409);
                });

    }

    @Test
    public void findUserById_WhenExistingId_returnsStatus200() {

        webTestClient.get()
                .uri("/api/v1/users/100")
                .exchange()
                .expectStatus().isOk() // Use HttpStatus para melhor legibilidade
                .expectBody(UserResponseDto.class)
                .consumeWith(result -> {
                    org.assertj.core.api.Assertions.assertThat(result.getResponseBody()).isNotNull();
                    org.assertj.core.api.Assertions.assertThat(result.getResponseBody().getId()).isEqualTo(100);
                    org.assertj.core.api.Assertions.assertThat(result.getResponseBody().getUsername()).isEqualTo("admin@techcorp.com");
                    org.assertj.core.api.Assertions.assertThat(result.getResponseBody().getRole()).isEqualTo("ADMIN");
                });

    }

    @Test
    public void findUserById_WhenNotFoundId_ReturnsNotFound() {

        webTestClient.get()
                .uri("/api/v1/users/99")
                .exchange()
                .expectStatus().isNotFound() // Use HttpStatus para melhor legibilidade
                .expectBody(ErrorMessage.class)
                .consumeWith(result -> {
                    org.assertj.core.api.Assertions.assertThat(result.getResponseBody()).isNotNull();
                    org.assertj.core.api.Assertions.assertThat(result.getResponseBody().getStatus()).isEqualTo(404);

                });

    }


}

