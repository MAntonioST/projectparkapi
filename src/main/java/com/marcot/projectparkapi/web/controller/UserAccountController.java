package com.marcot.projectparkapi.web.controller;


import com.marcot.projectparkapi.entity.UserAccount;
import com.marcot.projectparkapi.service.UserAccountService;
import com.marcot.projectparkapi.web.dto.UserAccountCreateDto;
import com.marcot.projectparkapi.web.dto.UserAccountPasswordDto;
import com.marcot.projectparkapi.web.dto.UserAccountResponseDto;
import com.marcot.projectparkapi.web.dto.mapper.UserAccountMapper;
import com.marcot.projectparkapi.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users")
@Tag(name = "User", description = "Endpoints for user management")
public class UserAccountController {

    private final UserAccountService userService;

    @Operation(summary = "Create a new user", description = "Creates a new user with the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully created",content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserAccountResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data provided", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "409", description = "Conflict - User already exists",content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity - Validation errors",content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PostMapping
    public ResponseEntity<UserAccountResponseDto> create(@Valid @RequestBody UserAccountCreateDto createDto) {
        UserAccount user = userService.save(UserAccountMapper.toUser(createDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserAccountMapper.toDto(user));
    }

    @Operation(summary = "Get user by ID", description = "Request requires a Bearer Token. Access restricted to ADMIN",
            security = @SecurityRequirement(name = "security"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserAccountResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar este recurso", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserAccountResponseDto> getById(@PathVariable Long id) {
        UserAccount user = userService.getById(id);
        return ResponseEntity.ok(UserAccountMapper.toDto(user));
    }

    @Operation(summary = "Update user password", description = "Request requires a Bearer Token. Access restricted to ADMIN",
            security = @SecurityRequirement(name = "security"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Password successfully updated",  content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid data provided", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity - Validation errors",content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar este recurso", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @Valid @RequestBody UserAccountPasswordDto dto) {
        UserAccount user = userService.updatePassword(id, dto.getCurrentPassword(), dto.getNewPassword(), dto.getConfirmPassword());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List all registered users", description = "Request requires a Bearer Token. Access restricted to ADMIN",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista com todos os usuários cadastrados",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = UserAccountResponseDto.class)))),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar este recurso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar este recurso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping
    public ResponseEntity<List<UserAccountResponseDto>> getAllUsers() {
        List<UserAccount> users = userService.findAllUsers();
        return ResponseEntity.ok(UserAccountMapper.toListDto(users));
    }

}