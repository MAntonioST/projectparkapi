package com.marcot.projectparkapi;


import com.marcot.projectparkapi.web.dto.ParkingCreateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/parkings/parking-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/parkings/parking-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ParkingIT {

    @Autowired
    WebTestClient testClient;

    @Test
    public void createCheckIn_WithValidData_ReturnCreatedAndLocationStatus201() {
        ParkingCreateDto createDto = ParkingCreateDto.builder()
                .licensePlate("WER-1111").brand("FIAT").model("PALIO 1.0")
                .color("BLUE").customerCpf("25125389072")
                .build();

        testClient.post().uri("/api/v1/parking/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alan@techcorp.com", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody()
                .jsonPath("licensePlate").isEqualTo("WER-1111")
                .jsonPath("brand").isEqualTo("FIAT")
                .jsonPath("model").isEqualTo("PALIO 1.0")
                .jsonPath("color").isEqualTo("BLUE")
                .jsonPath("customerCpf").isEqualTo("25125389072")
                .jsonPath("receiptNumber").exists()
                .jsonPath("entryTime").exists()
                .jsonPath("parkingSpaceCode").exists();
    }

    @Test
    public void createCheckIn_WithCustomerRole_ReturnErrorStatus403() {
        ParkingCreateDto createDto = ParkingCreateDto.builder()
                .licensePlate("WER-1111").brand("FIAT").model("PALIO 1.0")
                .color("BLUE").customerCpf("56296581076")
                .build();

        testClient.post().uri("/api/v1/parking/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "jane.smith@cyberdynetech.com", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody();

    }

    @Test
    public void createCheckIn_WithInvalidData_ReturnErrorStatus422() {
        ParkingCreateDto createDto = ParkingCreateDto.builder()
                .licensePlate("").brand("").model("")
                .color("").customerCpf("")
                .build();

        testClient.post().uri("/api/v1/parking/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alan@techcorp.com", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("status").isEqualTo("422")
                .jsonPath("path").isEqualTo("/api/v1/parking/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void createCheckIn_WithNonExistentCpf_ReturnErrorStatus404() {
        ParkingCreateDto createDto = ParkingCreateDto.builder()
                .licensePlate("WER-1111").brand("FIAT").model("PALIO 1.0")
                .color("BLUE").customerCpf("33838667000")
                .build();

        testClient.post().uri("/api/v1/parking/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alan@techcorp.com", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/parking/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Sql(scripts = "/sql/parkings/parking-insert-occupied-spaces.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/parkings/parking-delete-occupied-spaces.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    public void createCheckIn_WithOccupiedSpaces_ReturnErrorStatus404() {
        ParkingCreateDto createDto = ParkingCreateDto.builder()
                .licensePlate("WER-1111").brand("FIAT").model("PALIO 1.0")
                .color("BLUE").customerCpf("09191773016")
                .build();

        testClient.post().uri("/api/v1/parking/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alan@techcorp.com", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/parking/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void getCheckIn_WithAdminProfile_ReturnDataStatus200() {

        testClient.get()
                .uri("/api/v1/parking/check-in/{receipt}", "20230313-101300")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alan@techcorp.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("licensePlate").isEqualTo("FIT-1020")
                .jsonPath("brand").isEqualTo("FIAT")
                .jsonPath("model").isEqualTo("PALIO")
                .jsonPath("color").isEqualTo("GREEN")
                .jsonPath("customerCpf").isEqualTo("25125389072")
                .jsonPath("receiptNumber").isEqualTo("20230313-101300")
                .jsonPath("entryTime").isEqualTo("2023-03-13 10:15:00")
                .jsonPath("parkingSpaceCode").isEqualTo("A-01");
    }

    @Test
    public void getCheckIn_WithCustomerProfile_ReturnDataStatus200() {

        testClient.get()
                .uri("/api/v1/parking/check-in/{receipt}", "20230313-101300")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "john.doe@innovatech.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("licensePlate").isEqualTo("FIT-1020")
                .jsonPath("brand").isEqualTo("FIAT")
                .jsonPath("model").isEqualTo("PALIO")
                .jsonPath("color").isEqualTo("GREEN")
                .jsonPath("customerCpf").isEqualTo("25125389072")
                .jsonPath("receiptNumber").isEqualTo("20230313-101300")
                .jsonPath("entryTime").isEqualTo("2023-03-13 10:15:00")
                .jsonPath("parkingSpaceCode").isEqualTo("A-01");
    }

    @Test
    public void getCheckIn_WithNonExistentReceipt_ReturnErrorStatus404() {

        testClient.get()
                .uri("/api/v1/parking/check-in/{receipt}", "20230313-999999")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "john.doe@innovatech.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/parking/check-in/20230313-999999")
                .jsonPath("method").isEqualTo("GET");
    }

    @Test
    public void createCheckOut_WithExistingReceipt_ReturnSuccess() {

        testClient.put()
                .uri("/api/v1/parking/check-out/{receipt}", "20230313-101300")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alan@techcorp.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("licensePlate").isEqualTo("FIT-1020")
                .jsonPath("brand").isEqualTo("FIAT")
                .jsonPath("model").isEqualTo("PALIO")
                .jsonPath("color").isEqualTo("GREEN")
                .jsonPath("entryTime").isEqualTo("2023-03-13 10:15:00")
                .jsonPath("customerCpf").isEqualTo("25125389072")
                .jsonPath("parkingSpaceCode").isEqualTo("A-01")
                .jsonPath("receiptNumber").isEqualTo("20230313-101300")
                .jsonPath("exitTime").exists()
                .jsonPath("price").exists()
                .jsonPath("discount").exists();
    }

    @Test
    public void createCheckOut_WithNonExistentReceipt_ReturnErrorStatus404() {

        testClient.put()
                .uri("/api/v1/parking/check-out/{receipt}", "20230313-000000")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "alan@techcorp.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/parking/check-out/20230313-000000")
                .jsonPath("method").isEqualTo("PUT");
    }

    @Test
    public void createCheckOut_WithCustomerRole_ReturnErrorStatus403() {

        testClient.put()
                .uri("/api/v1/parking/check-out/{receipt}", "20230313-101300")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "jane.smith@cyberdynetech.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody();

    }
}