package com.readingtracker.boochive.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class RentalHistoryParameter {

    private final Long id;
    private final String bookIsbn;
    private final LocalDate rentalDate;
    private final LocalDate returnDate;
    private final String rentalFrom;
    private final String memo;
}
