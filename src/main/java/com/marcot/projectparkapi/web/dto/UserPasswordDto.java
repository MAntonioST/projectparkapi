package com.marcot.projectparkapi.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserPasswordDto {

    @NotBlank
    @Size(min = 6, max = 6,message = "The current password must be exactly 6 characters long")
    private String currentPassword;
    @NotBlank
    @Size(min = 6, max = 6, message = "The new password must be exactly 6 characters long")
    private String newPassword;
    @NotBlank
    @Size(min = 6, max = 6,message = "The confirmation of the password must be exactly 6 characters long")
    private String confirmPassword;
}