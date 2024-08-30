package com.readingtracker.boochive.dto;

import com.readingtracker.boochive.enums.ReadingStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadingBookRequest {

    private String bookIsbn;
    private Long collectionId;
    private ReadingStatus readingStatus;
}
