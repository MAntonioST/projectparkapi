package com.marcot.projectparkapi.web.dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserAccountResponseDto {

    private Long id;
    private String username;
    private String role;
}