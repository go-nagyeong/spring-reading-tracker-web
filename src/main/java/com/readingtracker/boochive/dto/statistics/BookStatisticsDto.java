package com.readingtracker.boochive.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class BookStatisticsDto {

    private final String bookIsbn;

    private final Long reviewCount; // 리뷰 개수

    private final BigDecimal averageRating; // 평균 평점

    private final Long readerCount; // 독자 수
}
