package com.readingtracker.boochive.dto;

import com.readingtracker.boochive.domain.PurchaseHistory;
import com.readingtracker.boochive.domain.RentalHistory;
import lombok.Getter;

@Getter
public class OwnHistoryDto {

    private PurchaseHistory purchase;
    private RentalHistory rental;
}
