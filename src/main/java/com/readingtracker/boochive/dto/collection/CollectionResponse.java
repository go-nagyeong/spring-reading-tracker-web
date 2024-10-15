package com.readingtracker.boochive.dto.collection;

import com.readingtracker.boochive.dto.book.BookDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CollectionResponse {

    private final Long id;

    private final String collectionName;

    // DTO 매핑 이후 조회 변수
    @Setter
    private List<BookDto> books; // 책 목록 (OneToMany 관계 ReadingBook의 Book 데이터)
}
