package com.readingtracker.boochive.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CollectionResponse {

    private final Long id;

    private final String collectionName;

    @Setter
    private List<BookDto> books; // 매핑 이후 조회 변수
}
