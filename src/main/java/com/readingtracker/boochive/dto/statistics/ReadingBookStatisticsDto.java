package com.readingtracker.boochive.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadingBookStatisticsDto {

    private final Long readingBookId;

    private final Integer userRating; // 사용자 리뷰 평점

    private final Long userReadCount; // 사용자 완독 횟수

    private final Long userNoteCount; // 사용자 독서 노트 수
}
