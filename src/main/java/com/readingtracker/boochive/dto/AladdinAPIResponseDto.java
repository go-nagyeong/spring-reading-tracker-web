package com.readingtracker.boochive.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AladdinAPIResponseDto {

    private Integer startIndex;
    private Integer totalResults;
    private Integer itemsPerPage;
    private Integer totalPages; // 전처리 후 변수
    private List<Item> item;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Item {

        private String title;
        private String author;
        private String formatAuthor; // 전처리 후 변수
        private String translator; // 전처리 후 변수
        private String publisher;
        private String pubDate;
        private String description;
        private String isbn;
        private String isbn13;
        private String cover;
        private String categoryName;
        private List<String> categoryList; // 전처리 후 변수
        private SubInfo subInfo;
        // 이후 조회 변수
        private int reviewCount;
        private double averageRating;
        private int readerCount;

        @Getter
        @Setter
        @NoArgsConstructor
        public static class SubInfo {

            private String subTitle;
            private String itemPage;
        }
    }
}