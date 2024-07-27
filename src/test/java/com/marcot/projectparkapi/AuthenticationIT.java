package com.marcot.projectparkapi;


import com.marcot.projectparkapi.jwt.JwtToken;
import com.marcot.projectparkapi.web.dto.UserAccountLoginDto;
import com.marcot.projectparkapi.web.exception.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/users/users-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/users/users-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class AuthenticationIT {

    @Autowired
    WebTestClient testClient;

    @Test
    public void authenticate_withValidCredentials_returnsTokenWithStatus200(){

        UserAccountLoginDto validUserLoginDto = new UserAccountLoginDto("alan@techcorp.com", "123456");

        JwtToken responseBody = testClient
                .post()
                .uri("/api/v1/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validUserLoginDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtToken.class)
                .returnResult().getResponseBody();
        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();

    }

    @Test
    public void authenticate_withInvalidCredentials_returnsErrorWithStatus400() {


        // Act & Assert
        testClient
                .post()
                .uri("/api/v1/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserAccountLoginDto("alan@techcorp.com", "000000"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorMessage.class)
                .value(responseBody -> {
                    org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
                    org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

                });



        // Act & Assert
        testClient
                .post()
                .uri("/api/v1/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserAccountLoginDto("invalid@email.com", "123456"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorMessage.class)
                .value(responseBody -> {
                    org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
                    org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

                });
    }

    @Test
    public void authenticate_withInvalidPassword_returnsErrorWithStatus422() {
        // Arrange
        UserAccountLoginDto invalidUserLoginDto1 = new UserAccountLoginDto("alan@techcorp.com", "1234"); // Empty username
        UserAccountLoginDto invalidUserLoginDto2 = new UserAccountLoginDto("alan@techcorp.com", ""); // Empty password

        // Act & Assert for empty username
        testClient
                .post()
                .uri("/api/v1/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidUserLoginDto1)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(ErrorMessage.class)
                .value(responseBody -> {
                    org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
                    org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
                });

        // Act & Assert for empty password
        testClient
                .post()
                .uri("/api/v1/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidUserLoginDto2)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(ErrorMessage.class)
                .value(responseBody -> {
                    org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
                    org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
                });
    }

    @Test
    public void authenticate_withInvalidUsername_returnsErrorWithStatus422() {
        // Arrange
        UserAccountLoginDto invalidUserLoginDto1 = new UserAccountLoginDto("", "123456"); // Empty username
        UserAccountLoginDto invalidUserLoginDto2 = new UserAccountLoginDto("@techcorp.com", "123456"); // Empty password

        // Act & Assert for empty username
        testClient
                .post()
                .uri("/api/v1/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidUserLoginDto1)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(ErrorMessage.class)
                .value(responseBody -> {
                    org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
                    org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
                });

        // Act & Assert for empty password
        testClient
                .post()
                .uri("/api/v1/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidUserLoginDto2)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(ErrorMessage.class)
                .value(responseBody -> {
                    org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
                    org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
                });
    }
}
