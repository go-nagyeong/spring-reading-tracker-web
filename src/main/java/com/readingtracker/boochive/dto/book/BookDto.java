package com.readingtracker.boochive.dto.book;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class BookDto {

    private String title;
    private String author;
    private String formatAuthor; // API 조회 결과 전처리 변수
    private String translator; // API 조회 결과 전처리 변수
    private String publisher;
    private String pubDate;
    private String description;
    private String isbn;
    private String isbn13;
    private String cover;
    private String categoryName;
    private List<String> categoryList; // API 조회 결과 전처리 변수
    private SubInfo subInfo;

    @Getter
    @Setter
    public static class SubInfo {

        private String subTitle;
        private String itemPage;

        // 이후 조회 변수 (책 검색 결과 화면 데이터)
        private BigDecimal averageRating;
        private Integer reviewCount;
        private Integer readerCount;

        // ---------- (유저 책장 화면 데이터)
        private Integer userRating;
        private Integer userNoteCount;
        private Integer userReadCount;
    }
}
