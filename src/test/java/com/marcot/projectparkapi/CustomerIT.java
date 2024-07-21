package com.marcot.projectparkapi;

import com.marcot.projectparkapi.web.dto.CustomerCreateDto;
import com.marcot.projectparkapi.web.dto.CustomerResponseDto;
import com.marcot.projectparkapi.web.dto.PageableDto;
import com.marcot.projectparkapi.web.exception.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/customers/customers-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/customers/customers-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class CustomerIT {

    @Autowired
    WebTestClient testClient;

    @Test
    public void createCustomer_WithValidData_ReturnCreatedCustomerWithStatus201() {
        // Given
        CustomerCreateDto createDto = new CustomerCreateDto("Alice Jones", "96015136049");

        // When
        CustomerResponseDto responseBody = testClient
                .post()
                .uri("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alice.jones@nextgen.com", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CustomerResponseDto.class)
                .returnResult().getResponseBody();

        // Then
        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getName()).isEqualTo(createDto.getName());
        org.assertj.core.api.Assertions.assertThat(responseBody.getCpf()).isEqualTo(createDto.getCpf());
    }

    @Test
    public void createCustomer_WithExistingCpf_ReturnStatus409() {
        // Given
        CustomerCreateDto createDto = new CustomerCreateDto("Alice Jones", "25125389072");

        // When
        ErrorMessage errorMessage = testClient
                .post()
                .uri("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alice.jones@nextgen.com", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        // Then
        org.assertj.core.api.Assertions.assertThat(errorMessage).isNotNull();
        org.assertj.core.api.Assertions.assertThat(errorMessage.getStatus()).isEqualTo(409);
        org.assertj.core.api.Assertions.assertThat(errorMessage.getMessage()).isNotEmpty();
    }

    @Test
    public void createCustomer_WithInvalidData_ReturnStatus422() {
        // Given
        CustomerCreateDto emptyNameDto = new CustomerCreateDto("", "96015136049");
        CustomerCreateDto emptyCpfDto = new CustomerCreateDto("Alice Jones", "");
        CustomerCreateDto invalidCpfFormatDto = new CustomerCreateDto("Alice Jones", "960.151.360-49");
        CustomerCreateDto invalidCpfDto = new CustomerCreateDto("Alice Jones", "00000000000");

        // When & Then
        testInvalidData(emptyNameDto);
        testInvalidData(emptyCpfDto);
        testInvalidData(invalidCpfFormatDto);
        testInvalidData(invalidCpfDto);
    }

    private void testInvalidData(CustomerCreateDto createDto) {
        ErrorMessage errorMessage = testClient
                .post()
                .uri("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alice.jones@nextgen.com", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(errorMessage).isNotNull();
        org.assertj.core.api.Assertions.assertThat(errorMessage.getStatus()).isEqualTo(422);
        org.assertj.core.api.Assertions.assertThat(errorMessage.getMessage()).isNotEmpty();
    }

    @Test
    public void createCustomer_WithUnauthorizedUser_ReturnStatus403() {
        // Given
        CustomerCreateDto createDto = new CustomerCreateDto("Alice Jones", "96015136049");

        // When & Then
        testClient
                .post()
                .uri("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alan@techcorp.com", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isEqualTo(403);
    }

    @Test
    public void findByCustomer_WithExistingIdByAdmin_ReturnCustomerWithStatus200() {
        // Given
        Long existingCustomerId = 10L;

        // When
        CustomerResponseDto responseBody = testClient
                .get()
                .uri("/api/v1/customers/{id}", existingCustomerId)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alan@techcorp.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerResponseDto.class)
                .returnResult().getResponseBody();

        // Then
        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(existingCustomerId);
        org.assertj.core.api.Assertions.assertThat(responseBody.getName()).isNotEmpty();
        org.assertj.core.api.Assertions.assertThat(responseBody.getCpf()).isNotEmpty();
    }

    @Test
    public void findByCustomer_WithNonExistingIdByAdmin_ReturnErrorMessageStatus404() {
        // Given
        Long nonExistingCustomerId = 0L;

        // When & Then
        testClient
                .get()
                .uri("/api/v1/customers/{id}", nonExistingCustomerId)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alan@techcorp.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();
    }

    @Test
    public void findByCustomer_WithIdExistingByAdmin_ReturnErrorMessageStatus403() {
        // Given
        Long existingCustomerId = 1L;

        // When & Then
        testClient
                .get()
                .uri("/api/v1/customers/{id}", existingCustomerId)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alice.jones@nextgen.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();
    }

    @Test
    public void findCustomers_WithPaginationByAdmin_ReturnCustomersWithStatus200() {
        // Given
        String adminUsername = "alan@techcorp.com";
        String adminPassword = "123456";

        // When
        PageableDto responseBody = testClient
                .get()
                .uri("/api/v1/customers")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, adminUsername, adminPassword))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getContent().size()).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(responseBody.getNumber()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(1);

        responseBody = testClient
                .get()
                .uri("/api/v1/customers?size=1&page=1")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, adminUsername, adminPassword))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getContent().size()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getNumber()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(2);

    }

    @Test
    public void findCustomers_WithPaginationByNotCustomer_ReturnStatus403() {
        // Given
        String customerUsername = "alice.jones@nextgen.com";
        String customerPassword = "123456";

        // When
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/customers")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, customerUsername, customerPassword))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult()
                .getResponseBody();
    }

    @Test
    public void fetchClient_WithCustomerTokenData_ReturnCustomerWithStatus200() {
        // Given
        String customerUsername = "jane.smith@cyberdynetech.com";
        String customerPassword = "123456";

        // When
        CustomerResponseDto responseBody = testClient
                .get()
                .uri("/api/v1/customers/details")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, customerUsername, customerPassword))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getCpf()).isEqualTo("56296581076");
        org.assertj.core.api.Assertions.assertThat(responseBody.getName()).isEqualTo("Jane Smith");
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(10);
    }

    @Test
    public void fetchCustomer_WithAdminTokenData_ReturnErrorMessageWithStatus403() {
        // Given
        String customerUsername = "alan@techcorp.com";
        String customerPassword = "123456";

        // When
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/customers/details")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, customerUsername, customerPassword))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult()
                .getResponseBody();

    }



}