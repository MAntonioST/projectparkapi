package com.marcot.projectparkapi.web.controller;

import com.marcot.projectparkapi.jwt.JwtToken;
import com.marcot.projectparkapi.jwt.JwtUserDetailsService;
import com.marcot.projectparkapi.web.dto.UserLoginDto;
import com.marcot.projectparkapi.web.dto.UserResponseDto;
import com.marcot.projectparkapi.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
@Tag(name = "User", description = "Endpoints for user management")
public class AuthenticatorController {

   private final JwtUserDetailsService detailsService;
   private final AuthenticationManager authenticationManager;


    /**
     * Authenticates a user with the provided credentials.
     *
     * @param dto     the user login data transfer object containing username and password
     * @param request the HTTP servlet request
     * @return a ResponseEntity containing a JWT token if authentication is successful, or an error message if it fails
     */
    @Operation(summary = "Authenticate a user", description = "Authenticates a user with the provided credentials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated and return Bearer token",content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid credentials",content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity - Validation errors",content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PostMapping("/auth")
    public ResponseEntity<?> authenticateUser(@RequestBody @Valid UserLoginDto dto, HttpServletRequest request) {

        log.info("Processo de autenticação pelo login {}",dto.getUsername());

        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(dto.getUsername(),dto.getPassword());
            authenticationManager.authenticate(authenticationToken);
            JwtToken token = detailsService.getTokenAuthenticate(dto.getUsername());
            return ResponseEntity.ok(token);
        }catch (AuthenticationException ex){
            log.warn("Bad Credentials from username '{}'",dto.getUsername());
        }
        return ResponseEntity
                .badRequest()
                .body(new ErrorMessage(request, HttpStatus.BAD_REQUEST, "Credentials inválid"));

    }

    // Adicione outros endpoints conforme necessário, como registro de usuários, logout, etc.
}