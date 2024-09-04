package com.readingtracker.boochive.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class PurchaseHistoryResponse {

    private final Long id;

    private final LocalDate purchaseDate;

    private final String purchaseFrom;

    private final BigDecimal price;

    private final String memo;
}
