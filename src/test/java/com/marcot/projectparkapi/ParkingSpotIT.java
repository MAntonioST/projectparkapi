package com.marcot.projectparkapi;


import com.marcot.projectparkapi.web.dto.ParkingSpotCreateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/parking-spot/parking_spot-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/parking-spot/parking_spot-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ParkingSpotIT {

    @Autowired
    WebTestClient testClient;

    @Test
    public void createSlot_WithValidData_ShouldReturnLocationStatus201() {
        testClient
                .post()
                .uri("/api/v1/parking-spots")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alan@techcorp.com", "123456"))
                .bodyValue(new ParkingSpotCreateDto("A-05", "FREE"))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION);
    }

    @Test
    public void createSlot_WithExistingCode_ShouldReturnErrorMessageWithStatus409() {
        testClient
                .post()
                .uri("/api/v1/parking-spots")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alan@techcorp.com", "123456"))
                .bodyValue(new ParkingSpotCreateDto("A-01", "FREE"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("status").isEqualTo(409)
                .jsonPath("method").isEqualTo("POST")
                .jsonPath("path").isEqualTo("/api/v1/parking-spots");
    }

    @Test
    public void createSlot_WithInvalidData_ShouldReturnErrorMessageWithStatus422() {
        testClient
                .post()
                .uri("/api/v1/parking-spots")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alan@techcorp.com", "123456"))
                .bodyValue(new ParkingSpotCreateDto("", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("status").isEqualTo(422)
                .jsonPath("method").isEqualTo("POST")
                .jsonPath("path").isEqualTo("/api/v1/parking-spots");

        testClient
                .post()
                .uri("/api/v1/parking-spots")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alan@techcorp.com", "123456"))
                .bodyValue(new ParkingSpotCreateDto("A-501", "FREE"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("status").isEqualTo(422)
                .jsonPath("method").isEqualTo("POST")
                .jsonPath("path").isEqualTo("/api/v1/parking-spots");
    }

    @Test
    public void getSlot_WithExistingCode_ShouldReturnSlotWithStatus200() {
        testClient
                .get()
                .uri("/api/v1/parking-spots/{code}", "A-01")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alan@techcorp.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("id").isEqualTo(10)
                .jsonPath("code").isEqualTo("A-01")
                .jsonPath("status").isEqualTo("FREE");
    }

    @Test
    public void getSlot_WithNonExistingCode_ShouldReturnErrorMessageWithStatus404() {
        testClient
                .get()
                .uri("/api/v1/parking-spots/{code}", "A-10")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alan@techcorp.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo(404)
                .jsonPath("method").isEqualTo("GET")
                .jsonPath("path").isEqualTo("/api/v1/parking-spots/A-10");
    }

    @Test
    public void getSlot_WithUserWithoutAccessPermission_ShouldReturnErrorMessageWithStatus403() {
        testClient
                .get()
                .uri("/api/v1/parking-spots/{code}", "A-01")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alice.jones@nextgen.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody();

    }

    @Test
    public void createSlot_WithUserWithoutAccessPermission_ShouldReturnErrorMessageWithStatus403() {
        testClient
                .post()
                .uri("/api/v1/parking-spots")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alice.jones@nextgen.com", "123456"))
                .bodyValue(new ParkingSpotCreateDto("A-05", "OCCUPIED"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody();

    }
}