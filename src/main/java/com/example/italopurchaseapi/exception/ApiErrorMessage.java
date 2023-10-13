package com.example.italopurchaseapi.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@AllArgsConstructor
@Data
public class ApiErrorMessage {
    private HttpStatus status;

    private List<String> errors;
}
