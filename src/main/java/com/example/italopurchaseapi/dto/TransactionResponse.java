package com.example.italopurchaseapi.dto;

import com.example.italopurchaseapi.dto.serializer.CurrencySerializer;
import com.example.italopurchaseapi.model.Transaction;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    public TransactionResponse(Transaction transaction) {
        this.id = transaction.getId();
        this.description = transaction.getDescription();
        this.date = transaction.getDate();
        this.amount = transaction.getAmount();
    }

    private int id;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC")
    private Date date;

    private BigDecimal amount;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSerialize(using = CurrencySerializer.class)
    private BigDecimal exchangedAmount;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSerialize(using = CurrencySerializer.class)
    private BigDecimal exchangeRate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC")
    private Date exchangeDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String exchangeCountryCurrency;
}
