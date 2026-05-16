package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {

    @NotBlank(message = "Username is required")
    @Size(max = 20, message = "Username must be 20 characters or fewer")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(max = 100, message = "Password must be 100 characters or fewer")
    private String password;
}
