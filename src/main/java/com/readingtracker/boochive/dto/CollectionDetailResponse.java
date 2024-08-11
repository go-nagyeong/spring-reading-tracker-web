package com.readingtracker.boochive.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CollectionDetailResponse {

    private Long id;
    private String collectionName;
    private List<BookParameter> books; // 매핑 이후 조회 변수
}
