package com.marcot.projectparkapi.web.controller;

import com.marcot.projectparkapi.entity.CustomerEntity;
import com.marcot.projectparkapi.jwt.JwtUserDetails;
import com.marcot.projectparkapi.repository.projection.CustomerProjection;
import com.marcot.projectparkapi.service.CustomerService;
import com.marcot.projectparkapi.web.dto.CustomerCreateDto;
import com.marcot.projectparkapi.web.dto.CustomerResponseDto;
import com.marcot.projectparkapi.web.dto.PageableDto;
import com.marcot.projectparkapi.web.dto.UserResponseDto;
import com.marcot.projectparkapi.web.dto.mapper.CustomerMapper;
import com.marcot.projectparkapi.web.dto.mapper.PageableMapper;
import com.marcot.projectparkapi.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/customers")
@Tag(name = "User", description = "Endpoints for customers management")
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "Create a new customer", description = "Resource to create a new customer linked to a registered user",
            security = @SecurityRequirement(name = "security"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer successfully created",content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Conflict - Customer CPF already exists",content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity - Validation errors",content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden: Customer creation is not allowed for ADMIN profile", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PostMapping
    public ResponseEntity<CustomerResponseDto> createCustomer(@RequestBody @Valid CustomerCreateDto dto) {
        CustomerEntity entity = customerService.createCustomer(dto);
        return ResponseEntity.status(201).body(CustomerMapper.toDto(entity));
    }

    @Operation(summary = "Find a customer", description = "Resource to find a customer by ID. " +
            "Request requires the use of a bearer token. Access restricted to Role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource successfully located",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = CustomerResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Customer not found",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed for CLIENT profile",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> getById(@PathVariable Long id) {
        CustomerEntity customerEntity = customerService.findById(id);
        return ResponseEntity.ok(CustomerMapper.toDto(customerEntity));
    }

    @Operation(summary = "Retrieve list of customers",
            description = "Request requires the use of a bearer token. Access restricted to Role='ADMIN'",
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
                            array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "name,asc")),
                            description = "Represents the sorting of the results. Multiple sorting criteria are supported."
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource successfully retrieved",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = CustomerResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed for CLIENT profile",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    )
            })
    @GetMapping
    public ResponseEntity<PageableDto> getAll(@Parameter(hidden = true) @PageableDefault(size = 5, sort = {"name"}) Pageable pageable) {
        Page<CustomerProjection> customers = customerService.getAllCustomers(pageable);
        return ResponseEntity.ok(PageableMapper.toDto(customers));
    }

    @Operation(summary = "Retrieve authenticated client data",
            description = "Request requires the use of a bearer token. Access restricted to Role='CLIENT'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource successfully retrieved",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = CustomerResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed for ADMIN profile",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    )
            })
    @GetMapping("/details")
    public ResponseEntity<CustomerResponseDto> getDetails(@AuthenticationPrincipal JwtUserDetails userDetails) {
        CustomerEntity customer = customerService.findByUserId(userDetails.getId());
        return ResponseEntity.ok(CustomerMapper.toDto(customer));
    }
}
