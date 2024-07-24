package com.readingtracker.boochive.dto;

import com.readingtracker.boochive.domain.ReadingStatus;
import lombok.Getter;

@Getter
public class ReadingBookFilterDto {

    private ReadingStatus readingStatus;
    private Long collectionId;
    private Boolean isOwned;

    public ReadingBookFilterDto(ReadingStatus readingStatus, Long collectionId, Boolean isOwned) {
        this.readingStatus = readingStatus;
        this.collectionId = collectionId;
        this.isOwned = isOwned;
    }
}
