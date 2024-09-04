package com.readingtracker.boochive.dto;

import com.readingtracker.boochive.enums.ReadingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ReadingBookResponse {

    private final Long id;

    private final String bookIsbn;

    private final Long collectionId;

    private final ReadingStatus readingStatus;

    // 매핑 이후 조회 변수
    @Setter
    private LocalDate readingStartDate;
    @Setter
    private BookDto bookInfo;
    @Setter
    private Boolean isOwned;
}
