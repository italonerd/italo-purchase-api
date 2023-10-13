package com.example.italopurchaseapi.client;

import com.example.italopurchaseapi.dto.treasury.CurrencyInformation;
import com.example.italopurchaseapi.dto.treasury.TreasuryReportingRatesExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

public class ExchangeServiceClient {
    private static final String EXCHANGE_SERVICE_BY_RANGE_URL = "https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange?fields=country_currency_desc,exchange_rate,record_date&filter=country_currency_desc:eq:%s,record_date:lte:%s,record_date:gte:%s";

    public List<CurrencyInformation> getExchangeCurrencyInformation(final String startDate, final String endDate, final String countryCurrency) {
        String url = String.format(EXCHANGE_SERVICE_BY_RANGE_URL, countryCurrency, endDate, startDate);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<TreasuryReportingRatesExchange> response = restTemplate.getForEntity(url, TreasuryReportingRatesExchange.class);

        return Objects.requireNonNull(response.getBody()).getData();
    }
}
