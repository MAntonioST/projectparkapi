package com.marcot.projectparkapi.web.controller;


import com.marcot.projectparkapi.entity.ParkingSpotEntity;
import com.marcot.projectparkapi.service.ParkingSpotService;
import com.marcot.projectparkapi.web.dto.ParkingSpotCreateDto;
import com.marcot.projectparkapi.web.dto.ParkingSpotResponseDto;
import com.marcot.projectparkapi.web.dto.mapper.ParkingSpotMapper;
import com.marcot.projectparkapi.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Parking Spots", description = "Contains all operations related to the parking spot resource")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/parking-spots")
public class ParkingSpotController {

    private final ParkingSpotService parkingSpotService;

    @Operation(summary = "Create a new parking spot", description = "Resource to create a new parking spot." +
            "Request requires the use of a bearer token. Access restricted to Role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Resource successfully created",
                            headers = @Header(name = HttpHeaders.LOCATION, description = "URL of the created resource")),
                    @ApiResponse(responseCode = "409", description = "Parking spot already registered",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Resource not processed due to lack of data or invalid data",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed for CLIENT role",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    )
            })
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid ParkingSpotCreateDto dto) {
        ParkingSpotEntity parkingSpot = ParkingSpotMapper.toParkingSpot(dto);
        parkingSpotService.save(parkingSpot);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri().path("/{code}")
                .buildAndExpand(parkingSpot.getCode())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Find a parking spot", description = "Resource to return a parking spot by its code" +
            "Request requires the use of a bearer token. Access restricted to Role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource successfully retrieved",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ParkingSpotResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Parking spot not found",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed for CLIENT role",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    )
            })
    @GetMapping("/{code}")
    public ResponseEntity<ParkingSpotResponseDto> getByCode(@PathVariable String code) {
        ParkingSpotEntity parkingSpot = parkingSpotService.findByCode(code);
        return ResponseEntity.ok(ParkingSpotMapper.toDto(parkingSpot));
    }
}
