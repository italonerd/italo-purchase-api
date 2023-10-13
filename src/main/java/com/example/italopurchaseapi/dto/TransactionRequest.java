package com.example.italopurchaseapi.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TransactionRequest {
    @NotBlank(message = "description can't be null or empty.")
    @Size(max = 20, message = "description can't have more than 20 characters.")
    private String description;

    @NotNull(message = "date can't be null or empty.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    @NotNull(message = "amount can't be null or empty.")
    @Digits(integer = 9, fraction = 2, message = "invalid amount, it must contain maximum of two fraction digits.")
    @DecimalMin(value = "0.0", inclusive = false, message = "amount should be bigger than zero.")
    private BigDecimal amount;
}

