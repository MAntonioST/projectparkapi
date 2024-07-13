package com.marcot.projectparkapi.web.controller;

import com.marcot.projectparkapi.entity.CustomerEntity;
import com.marcot.projectparkapi.service.CustomerService;
import com.marcot.projectparkapi.web.dto.CustomerCreateDto;
import com.marcot.projectparkapi.web.dto.CustomerResponseDto;
import com.marcot.projectparkapi.web.dto.UserResponseDto;
import com.marcot.projectparkapi.web.dto.mapper.CustomerMapper;
import com.marcot.projectparkapi.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/customers")
@Tag(name = "User", description = "Endpoints for customers management")
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "Create a new customer", description = "Resource to create a new customer linked to a registered user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer successfully created",content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data provided", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "409", description = "Conflict - Customer already exists",content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity - Validation errors",content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PostMapping
    public ResponseEntity<CustomerResponseDto> createCustomer(@RequestBody @Valid CustomerCreateDto dto) {
        CustomerEntity entity = customerService.save(dto);
        return ResponseEntity.status(201).body(CustomerMapper.toDto(entity));
    }
}
