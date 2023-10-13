package com.example.italopurchaseapi.service;

import com.example.italopurchaseapi.client.ExchangeServiceClient;
import com.example.italopurchaseapi.dto.TransactionRequest;
import com.example.italopurchaseapi.dto.TransactionResponse;
import com.example.italopurchaseapi.dto.treasury.CurrencyInformation;
import com.example.italopurchaseapi.exception.NotFoundException;
import com.example.italopurchaseapi.model.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {

    private static final String COUNTRY_CURRENCY = "Euro Zone-Euro";

    @Mock
    ExchangeServiceClient exchangeServiceClient;
    @InjectMocks
    TransactionService transactionService;
    private List<Transaction> transactions;

    @Before
    public void setUp() {
        transactionService = new TransactionService();
        transactionService.setExchangeServiceClient(exchangeServiceClient);
        Transaction transaction1 = new Transaction(0, "Transaction one!", new Date(), BigDecimal.TWO);
        Transaction transaction2 = new Transaction(1, "Transaction two!", new Date(), BigDecimal.TEN);
        Transaction transaction3 = new Transaction(2, "Transaction three!", new Date(), BigDecimal.ONE);
        transactions = newArrayList(transaction1, transaction2, transaction3);
        TransactionService.transactions = transactions;
    }

    @Test
    public void shouldFindTransactionById() {
        Transaction transactionResult = transactionService.findById(transactions.get(0).getId());
        assertThat(transactionResult).isEqualTo(transactions.get(0));
    }

    @Test
    public void failToFindTransactionById() {
        Exception notFoundException = assertThrows(NotFoundException.class, () -> {
            transactionService.findById(-1);
        });

        String expectedMessage = "Transaction not found";
        String actualMessage = notFoundException.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    public void shouldSaveTransaction() {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setDescription(transactions.getFirst().getDescription());
        transactionRequest.setDate(transactions.getFirst().getDate());
        transactionRequest.setAmount(transactions.getFirst().getAmount());

        TransactionResponse transaction = transactionService.save(transactionRequest);

        assertThat(transaction.getId()).isZero();
        assertThat(transaction.getDescription()).isEqualTo(transactions.getFirst().getDescription());
        assertThat(transaction.getDate()).isEqualTo(transactions.getFirst().getDate());
        assertThat(transaction.getAmount()).isEqualTo(transactions.getFirst().getAmount());
    }

    @Test
    public void shouldGetTransactionWithExchangeValues() {
        CurrencyInformation currencyInformation = new CurrencyInformation("Euro Zone-Euro", BigDecimal.TWO, new Date());
        List<CurrencyInformation> treasuryReportingRatesExchange = List.of(currencyInformation);
        when(exchangeServiceClient.getExchangeCurrencyInformation(anyString(), anyString(), anyString())).thenReturn(treasuryReportingRatesExchange);

        TransactionResponse transaction =
                transactionService.getTransactionWithExchangeValues(transactions.getFirst().getId(), COUNTRY_CURRENCY);

        assertThat(transaction.getId()).isZero();
        assertThat(transaction.getDescription()).isEqualTo(transactions.getFirst().getDescription());
        assertThat(transaction.getDate()).isEqualTo(transactions.getFirst().getDate());
        assertThat(transaction.getAmount()).isEqualTo(transactions.getFirst().getAmount());
        assertThat(transaction.getExchangedAmount()).isEqualTo(new BigDecimal(4));
        assertThat(transaction.getExchangeRate()).isEqualTo(currencyInformation.getExchangeRate());
        assertThat(transaction.getExchangeDate()).isEqualTo(currencyInformation.getRecordDate());
        assertThat(transaction.getExchangeCountryCurrency()).isEqualTo(currencyInformation.getCountryCurrencyDesc());


    }

    @Test
    public void failToGetTransactionWithExchangeValues() {
        when(exchangeServiceClient.getExchangeCurrencyInformation(anyString(), anyString(), anyString())).thenReturn(Collections.emptyList());

        Exception notFoundException = assertThrows(NotFoundException.class, () -> {
            transactionService.getTransactionWithExchangeValues(transactions.getFirst().getId(), COUNTRY_CURRENCY);
        });

        String expectedMessage = "Transaction exchange values not found";
        String actualMessage = notFoundException.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }
}
