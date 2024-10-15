package com.readingtracker.boochive.dto.record;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ReadingRecordResponse {

    private final Long id;

    private final LocalDate startDate;

    private final LocalDate endDate;

    // DTO 매핑 이후 조회 변수
    @Setter
    private Long dDay; // 독서 시작일부터의 디데이
}
