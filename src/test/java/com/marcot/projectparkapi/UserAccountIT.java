package com.marcot.projectparkapi;

import com.marcot.projectparkapi.web.dto.UserAccountCreateDto;
import com.marcot.projectparkapi.web.dto.UserAccountPasswordDto;
import com.marcot.projectparkapi.web.dto.UserAccountResponseDto;
import com.marcot.projectparkapi.web.exception.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/users/users-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/users/users-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserAccountIT {

    @Autowired
    WebTestClient testClient;

    @Test
    public void createUser_WithValidUsernameAndPassword_ReturnsCreatedUserWithStatus201() {
        UserAccountResponseDto responseBody = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserAccountCreateDto("tody@email.com", "123456"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserAccountResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getUsername()).isEqualTo("tody@email.com");
        org.assertj.core.api.Assertions.assertThat(responseBody.getRole()).isEqualTo("CLIENTE");
    }

    @Test
    public void createUser_WithInvalidUsername_ReturnsErrorMessageWithStatus422() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserAccountCreateDto("", "123456"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserAccountCreateDto("tody@", "123456"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserAccountCreateDto("tody@email", "123456"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createUser_WithInvalidPassword_ReturnsErrorMessageWithStatus422() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserAccountCreateDto("tody@email.com", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserAccountCreateDto("tody@email.com", "123"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserAccountCreateDto("tody@email.com", "123456789"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createUser_WithDuplicateUsername_ReturnsErrorMessageWithStatus409() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserAccountCreateDto("john.doe@innovatech.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void findUser_WithExistingId_ReturnsUserWithStatus200() {
        UserAccountResponseDto responseBody = testClient
                .get()
                .uri("/api/v1/users/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alan@techcorp.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserAccountResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(100);
        org.assertj.core.api.Assertions.assertThat(responseBody.getUsername()).isEqualTo("alan@techcorp.com");
        org.assertj.core.api.Assertions.assertThat(responseBody.getRole()).isEqualTo("ADMIN");

        responseBody = testClient
                .get()
                .uri("/api/v1/users/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alan@techcorp.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserAccountResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(101);
        org.assertj.core.api.Assertions.assertThat(responseBody.getUsername()).isEqualTo("john.doe@innovatech.com");
        org.assertj.core.api.Assertions.assertThat(responseBody.getRole()).isEqualTo("CLIENTE");

        responseBody = testClient
                .get()
                .uri("/api/v1/users/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "john.doe@innovatech.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserAccountResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(101);
        org.assertj.core.api.Assertions.assertThat(responseBody.getUsername()).isEqualTo("john.doe@innovatech.com");
        org.assertj.core.api.Assertions.assertThat(responseBody.getRole()).isEqualTo("CLIENTE");
    }

    @Test
    public void findUser_WithNonExistingId_ReturnsErrorMessageWithStatus404() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/users/0")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alan@techcorp.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

    }

    @Test
    public void findUser_WithClientUserSearchingAnotherClient_ReturnsErrorMessageWithStatus403() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/users/102")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "john.doe@innovatech.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

    }

    @Test
    public void updatePassword_WithValidData_ReturnsStatus204() {
        testClient
                .patch()
                .uri("/api/v1/users/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alan@techcorp.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserAccountPasswordDto("123456", "123456", "123456"))
                .exchange()
                .expectStatus().isNoContent();

        testClient
                .patch()
                .uri("/api/v1/users/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "john.doe@innovatech.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserAccountPasswordDto("123456", "123456", "123456"))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void updatePassword_WithDifferentUsers_ReturnsErrorMessageWithStatus403() {
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/users/103")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "john.doe@innovatech.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserAccountPasswordDto("123456", "123456", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();
    }

    @Test
    public void updatePassword_WithInvalidFields_ReturnsErrorMessageWithStatus422() {
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/users/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alan@techcorp.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserAccountPasswordDto("", "", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .patch()
                .uri("/api/v1/users/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alan@techcorp.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserAccountPasswordDto("12345", "12345", "12345"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .patch()
                .uri("/api/v1/users/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alan@techcorp.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserAccountPasswordDto("12345678", "12345678", "12345678"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void updatePassword_WithInvalidPasswords_ReturnsErrorMessageWithStatus400() {
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/users/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alan@techcorp.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserAccountPasswordDto("123456", "123456", "000000"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);

        responseBody = testClient
                .patch()
                .uri("/api/v1/users/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alan@techcorp.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserAccountPasswordDto("000000", "123456", "123456"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);
    }

    @Test
    public void listUsers_WithUserWithPermission_ReturnsListOfUsersWithStatus200() {
        List<UserAccountResponseDto> responseBody = testClient
                .get()
                .uri("/api/v1/users")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alan@techcorp.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserAccountResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.size()).isEqualTo(5);
    }

    @Test
    public void listUsers_WithUserWithoutPermission_ReturnsErrorMessageWithStatus403() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/users")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "john.doe@innovatech.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

    }

}

