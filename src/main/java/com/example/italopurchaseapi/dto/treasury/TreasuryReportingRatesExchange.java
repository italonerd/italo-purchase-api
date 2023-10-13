package com.example.italopurchaseapi.dto.treasury;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreasuryReportingRatesExchange {
    private List<CurrencyInformation> data;
}