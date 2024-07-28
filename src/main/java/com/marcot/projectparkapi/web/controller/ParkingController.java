package com.marcot.projectparkapi.web.controller;


import com.marcot.projectparkapi.entity.CustomerParkingSpace;
import com.marcot.projectparkapi.jwt.JwtUserDetails;
import com.marcot.projectparkapi.repository.projection.CustomerParkingSpaceProjection;
import com.marcot.projectparkapi.service.CustomerParkingSpaceService;
import com.marcot.projectparkapi.service.ParkingService;
import com.marcot.projectparkapi.web.dto.PageableDto;
import com.marcot.projectparkapi.web.dto.ParkingCreateDto;
import com.marcot.projectparkapi.web.dto.ParkingResponseDto;
import com.marcot.projectparkapi.web.dto.mapper.CustomerParkingSpaceMapper;
import com.marcot.projectparkapi.web.dto.mapper.PageableMapper;
import com.marcot.projectparkapi.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

@Tag(name = "Parking", description = "Operations for vehicle check-in and check-out in the parking lot.")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/parking")
public class ParkingController {
    private final ParkingService parkingService;
    private final CustomerParkingSpaceService customerParkingSpaceService;

    @Operation(summary = "Check-in operation", description = "Resource to check-in a vehicle into the parking lot. " +
            "Request requires a bearer token. Access restricted to Role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Resource created successfully",
                            headers = @Header(name = HttpHeaders.LOCATION, description = "URL to access the created resource"),
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ParkingResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Possible causes: <br/>" +
                            "- Customer's CPF not registered in the system; <br/>" +
                            "- No available parking spot found;",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Resource not processed due to missing or invalid data",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed for CUSTOMER role",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PostMapping("/check-in")
    public ResponseEntity<ParkingResponseDto> checkin(@RequestBody @Valid ParkingCreateDto dto) {
        CustomerParkingSpace customerParkingSpot = CustomerParkingSpaceMapper.toCustomerParkingSpace(dto);
        parkingService.checkIn(customerParkingSpot);
        ParkingResponseDto responseDto = CustomerParkingSpaceMapper.toDto(customerParkingSpot);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri().path("/{receipt}")
                .buildAndExpand(customerParkingSpot.getReceiptNumber())
                .toUri();
        return ResponseEntity.created(location).body(responseDto);
    }

    @Operation(summary = "Find a parked vehicle", description = "Resource to retrieve a parked vehicle " +
            "by the receipt number. Request requires a bearer token.",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = PATH, name = "receipt", description = "Receipt number generated during check-in")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource found successfully",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ParkingResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Receipt number not found.",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping("/check-in/{receipt}")
    public ResponseEntity<ParkingResponseDto> getByReceipt(@PathVariable String receipt) {
        CustomerParkingSpace customerParkingSpot = customerParkingSpaceService.findByReceiptNumber(receipt);
        ParkingResponseDto dto = CustomerParkingSpaceMapper.toDto(customerParkingSpot);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Check-out operation", description = "Resource to check-out a vehicle from the parking lot. " +
            "Request requires a bearer token. Access restricted to Role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            parameters = { @Parameter(in = PATH, name = "receipt", description = "Receipt number generated during check-in",
                    required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource updated successfully",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ParkingResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Receipt number does not exist or " +
                            "the vehicle has already been checked out.",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed for CUSTOMER role",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PutMapping("/check-out/{receipt}")
    public ResponseEntity<ParkingResponseDto> checkout(@PathVariable String receipt) {
        CustomerParkingSpace customerParkingSpace = parkingService.checkOut(receipt);
        ParkingResponseDto dto = CustomerParkingSpaceMapper.toDto(customerParkingSpace);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Find customer's parking records by CPF", description = "Find customer's " +
            "parking records by CPF. Request requires a bearer token.",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = PATH, name = "cpf", description = "Customer's CPF to be searched",
                            required = true
                    ),
                    @Parameter(in = QUERY, name = "page", description = "Represents the returned page",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0"))
                    ),
                    @Parameter(in = QUERY, name = "size", description = "Represents the total elements per page",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "5"))
                    ),
                    @Parameter(in = QUERY, name = "sort", description = "Default sorting field 'entryDate,asc'. ",
                            array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "entryDate,asc")),
                            hidden = true
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource found successfully",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = PageableDto.class))),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed for CUSTOMER role",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<PageableDto> getAllParkingRecordsByCpf(@PathVariable String cpf, @Parameter(hidden = true)
    @PageableDefault(size = 5, sort = "entryDate",
            direction = Sort.Direction.ASC) Pageable pageable) {
        Page<CustomerParkingSpaceProjection> projection = customerParkingSpaceService.findAllByCustomerCpf(cpf, pageable);
        PageableDto dto = PageableMapper.toDto(projection);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Find logged-in customer's parking records",
            description = "Find logged-in customer's parking records. " +
                    "Request requires a bearer token.",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = QUERY, name = "page",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                            description = "Represents the returned page"
                    ),
                    @Parameter(in = QUERY, name = "size",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "5")),
                            description = "Represents the total elements per page"
                    ),
                    @Parameter(in = QUERY, name = "sort", hidden = true,
                            array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "entryDate,asc")),
                            description = "Default sorting field 'entryDate,asc'. ")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource found successfully",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ParkingResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed for ADMIN role",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping
    public ResponseEntity<PageableDto> getAllParkingRecordsForLoggedInCustomer(@AuthenticationPrincipal JwtUserDetails user,
                                                                               @Parameter(hidden = true) @PageableDefault(
                                                                                       size = 5, sort = "entryDate",
                                                                                       direction = Sort.Direction.ASC) Pageable pageable) {

        Page<CustomerParkingSpaceProjection> projection = customerParkingSpaceService.findAllByUserAccountId(user.getId(), pageable);
        PageableDto dto = PageableMapper.toDto(projection);
        return ResponseEntity.ok(dto);
    }
}