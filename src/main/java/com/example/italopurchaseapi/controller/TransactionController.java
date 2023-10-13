package com.example.italopurchaseapi.controller;

import com.example.italopurchaseapi.dto.TransactionRequest;
import com.example.italopurchaseapi.dto.TransactionResponse;
import com.example.italopurchaseapi.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "transactions")
@Validated
public class TransactionController {

    @Autowired
    private TransactionService service;

    @PostMapping(consumes = "application/json")
    public @ResponseBody TransactionResponse saveTransaction(@Valid @RequestBody final TransactionRequest transaction) {
        return service.save(transaction);
    }

    @GetMapping(value = "/{id}/{exchangeCountryCurrency}")
    public @ResponseBody TransactionResponse getTransactionWithExchangeValues(@PathVariable int id, @PathVariable String exchangeCountryCurrency) {
        return service.getTransactionWithExchangeValues(id, exchangeCountryCurrency);
    }

}
