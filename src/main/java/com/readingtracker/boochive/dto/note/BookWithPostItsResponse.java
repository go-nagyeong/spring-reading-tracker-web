package com.readingtracker.boochive.dto.note;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BookWithPostItsResponse {

    private final Long readingBookId;

    private final String bookIsbn;

    private final String title;

    private final String cover;

    // DTO 매핑 이후 조회 변수
    @Setter
    private List<PostItResponse> postIts; // 포스트잇 목록 (OneToMany 관계 ReadingNote 데이터 조인)
}
