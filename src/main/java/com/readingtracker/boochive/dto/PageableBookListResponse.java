package com.readingtracker.boochive.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PageableBookListResponse {

    private Integer startIndex;
    private Integer totalResults;
    private Integer itemsPerPage;
    private Integer totalPages; // API 조회 결과 전처리 변수

    private Integer errorCode;
    private String errorMessage;

    private List<BookDto> item;
}
