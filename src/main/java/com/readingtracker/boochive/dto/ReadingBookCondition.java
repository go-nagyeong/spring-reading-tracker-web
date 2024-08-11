package com.readingtracker.boochive.dto;

import com.readingtracker.boochive.domain.ReadingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadingBookCondition {

    private final ReadingStatus readingStatus;
    private final Long collectionId;
    private final Boolean isOwned;
}
