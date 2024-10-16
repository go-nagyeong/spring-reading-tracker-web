package com.readingtracker.boochive.dto.reading;

import com.readingtracker.boochive.dto.book.BookDto;
import com.readingtracker.boochive.dto.statistics.ReadingBookStatisticsDto;
import com.readingtracker.boochive.enums.ReadingStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ReadingBookResponse {

    private final Long id;

    private final String bookIsbn;

    private final Long collectionId;

    private final ReadingStatus readingStatus;

    /**
     * DTO 매핑 이후 조회 변수
     */
    @Setter
    private BookDto bookInfo; // 책 상세 정보 (ManyToOne 관계 Book 데이터 조인)

    // (책 상세 페이지 데이터)
    @Setter
    private LocalDate readingStartDate; // 가장 최근 이력의 독서 시작일 (OneToMany 관계 ReadingRecord 데이터 조인)

    // (사용자 책장(독서 목록) 페이지 데이터)
    @Setter
    private Boolean isOwned; // 책 소장 여부 (PurchaseHistory)
    @Setter
    private ReadingBookStatisticsDto statistics; // 사용자 독서 통계 정보
}
