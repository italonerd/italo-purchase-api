package com.example.italopurchaseapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
public class Transaction {
    private int id;

    private String description;

    private Date date;

    private BigDecimal amount;
}
