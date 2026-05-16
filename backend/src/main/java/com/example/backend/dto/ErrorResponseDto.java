package com.example.backend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ErrorResponseDto {

    private LocalDateTime timestamp;

    private Integer status;

    // Short HTTP reason phrase, e.g. "Not Found"
    private String error;

    // Human-readable detail message
    private String message;

    // Request path that produced the error, e.g. "/api/movies/999"
    private String path;
}
