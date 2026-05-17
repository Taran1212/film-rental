package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class NewActorDto {

    @NotBlank(message = "First name is required")
    @Size(max = 45, message = "First name must be 45 characters or fewer")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 45, message = "Last name must be 45 characters or fewer")
    private String lastName;
}
