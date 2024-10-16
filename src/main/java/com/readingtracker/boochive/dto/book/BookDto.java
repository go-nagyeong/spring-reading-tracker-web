package com.readingtracker.boochive.dto.book;

import com.readingtracker.boochive.dto.statistics.BookStatisticsDto;
import lombok.Getter;
import lombok.Setter;

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
    }

    // DTO 매핑 이후 조회 변수
    private BookStatisticsDto statistics; // 책 통계 정보
}
