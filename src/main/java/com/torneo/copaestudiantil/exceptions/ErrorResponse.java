package com.torneo.copaestudiantil.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private int statusCode;
    private LocalDateTime timestamp;
    private String errorDetails;

    public ErrorResponse(String message, int statusCode, String errorDetails) {
        this.message = message;
        this.statusCode = statusCode;
        this.errorDetails = errorDetails;
    }
}
