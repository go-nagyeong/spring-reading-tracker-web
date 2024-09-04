package com.readingtracker.boochive.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class RentalHistoryResponse {

    private final Long id;

    private final LocalDate rentalDate;

    private final LocalDate returnDate;

    private final String rentalFrom;

    private final String memo;
}
