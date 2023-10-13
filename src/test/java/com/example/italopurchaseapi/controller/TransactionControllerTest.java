package com.example.italopurchaseapi.controller;

import com.example.italopurchaseapi.model.Transaction;
import com.example.italopurchaseapi.service.TransactionService;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.assertj.core.util.Lists.newArrayList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setUp() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Transaction transaction1 = new Transaction(0, "Transaction one!", simpleDateFormat.parse("2023-01-01"), BigDecimal.TWO);
        Transaction transaction2 = new Transaction(1, "Transaction two!", simpleDateFormat.parse("2023-12-12"), BigDecimal.TEN);
        Transaction transaction3 = new Transaction(2, "Transaction three!", simpleDateFormat.parse("2030-12-12"), BigDecimal.ONE);
        TransactionService.transactions = newArrayList(transaction1, transaction2, transaction3);
    }

    @Test
    public void shouldSaveTransactionById() throws Exception {
        String jsonRequestBody = "{\"description\": \"12345678901234567890\", \"date\": \"2024-10-12\",\"amount\": 100.10}";
        this.mockMvc.perform(
                        post("/transactions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequestBody)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(0)))
                .andExpect(jsonPath("$.description", Is.is("12345678901234567890")))
                .andExpect(jsonPath("$.date", Is.is("2024-10-12")))
                .andExpect(jsonPath("$.amount", Is.is(100.10)));
    }

    @Test
    public void failToSaveTransactionById() throws Exception {
        String jsonRequestBody = "{\"description\": \"123456789012345678901\", \"date\": \"2024-10-1a2\", \"amount\": 100.103}";
        this.mockMvc.perform(
                        post("/transactions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequestBody)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", Is.is("BAD_REQUEST")));
    }

    @Test
    public void shouldGetTransactionWithExchangeValues() throws Exception {
        this.mockMvc.perform(
                        get("/transactions/0/Euro Zone-Euro")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(0)))
                .andExpect(jsonPath("$.description", Is.is("Transaction one!")))
                .andExpect(jsonPath("$.date", Is.is("2023-01-01")))
                .andExpect(jsonPath("$.amount", Is.is(2)));
    }

    @Test
    public void failGetTransactionWithExchangeValues_noTransactionFound() throws Exception {
        this.mockMvc.perform(
                        get("/transactions/9/Euro Zone-Euro")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", Is.is("NOT_FOUND")))
                .andExpect(jsonPath("$.errors[0]", Is.is("Transaction not found")));
    }

    @Test
    public void failGetTransactionWithExchangeValues_noExchangeValueFound() throws Exception {
        this.mockMvc.perform(
                        get("/transactions/2/Euro Zone-Euro")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", Is.is("NOT_FOUND")))
                .andExpect(jsonPath("$.errors[0]", Is.is("Transaction exchange values not found")));
    }
}
