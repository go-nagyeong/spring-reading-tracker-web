package com.readingtracker.boochive.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class PurchaseHistoryParameter {

    private final Long id;
    private final String bookIsbn;
    private final LocalDate purchaseDate;
    private final String purchaseFrom;
    private final Integer price;
    private final String memo;
}
