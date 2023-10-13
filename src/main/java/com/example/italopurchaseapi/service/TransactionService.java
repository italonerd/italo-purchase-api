package com.example.italopurchaseapi.service;

import com.example.italopurchaseapi.client.ExchangeServiceClient;
import com.example.italopurchaseapi.dto.TransactionRequest;
import com.example.italopurchaseapi.dto.TransactionResponse;
import com.example.italopurchaseapi.dto.treasury.CurrencyInformation;
import com.example.italopurchaseapi.exception.NotFoundException;
import com.example.italopurchaseapi.model.Transaction;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class TransactionService {

    public static int transactionsId = 0;
    public static List<Transaction> transactions = new ArrayList<>();

    @Setter
    public ExchangeServiceClient exchangeServiceClient = new ExchangeServiceClient();

    public Transaction findById(int id) {
        var transaction = transactions.stream().filter(a -> a.getId() == id).findFirst();
        if (transaction.isEmpty()) {
            throw new NotFoundException("Transaction not found");
        }
        return transaction.get();
    }

    public TransactionResponse save(TransactionRequest request) {
        Transaction entity = new Transaction(transactionsId++, request.getDescription(), request.getDate(), request.getAmount());
        transactions.add(entity);
        return new TransactionResponse(entity);
    }

    public TransactionResponse getTransactionWithExchangeValues(final int id, final String countryCurrency) {
        TransactionResponse transaction = new TransactionResponse(findById(id));
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDateExchange = transaction.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate startDateExchange = endDateExchange.minusMonths(6);

        List<CurrencyInformation> treasuryReportingRatesExchange = exchangeServiceClient.getExchangeCurrencyInformation(startDateExchange.format(dateTimeFormatter), endDateExchange.format(dateTimeFormatter), countryCurrency);
        if (treasuryReportingRatesExchange.isEmpty()) {
            throw new NotFoundException("Transaction exchange values not found");
        } else {
            var currencyInformation = treasuryReportingRatesExchange.stream().max(Comparator.comparing(CurrencyInformation::getRecordDate));
            transaction.setExchangeCountryCurrency(currencyInformation.get().getCountryCurrencyDesc());
            transaction.setExchangeDate(currencyInformation.get().getRecordDate());
            transaction.setExchangeRate(currencyInformation.get().getExchangeRate());
            transaction.setExchangedAmount(transaction.getAmount().multiply(currencyInformation.get().getExchangeRate()));
        }

        return transaction;
    }


}
