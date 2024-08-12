package com.readingtracker.boochive.dto;

import com.readingtracker.boochive.enums.ReadingStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadingBookDetailResponse {

    private Long id;
    private String bookIsbn;
    private Long collectionId;
    private ReadingStatus readingStatus;
    private BookParameter bookInfo; // 매핑 이후 조회 변수
    private Boolean isOwned; // 매핑 이후 조회 변수
}
