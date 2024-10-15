package com.readingtracker.boochive.dto.record;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ReadingRecordRequest {

    private final Long id; // Batch 작업을 위해 각 요청 데이터에 id 값이 들어감 (일반적인 경우에는 URL에 id값 포함으로 필요 X)

    // NOTE: (241015) 독서 이력 신규 생성은 사용자 개입 없이 비즈니스 로직에 따라 서버 측에서 자동으로 이루어지 때문에 해당 필드 사용 X
//    @NotNull(message = "도서 ID " + AppConstants.UNKNOWN_INVALID_ARG_ERROR_MSG, groups = ValidationGroups.Create.class)
//    private final Long readingBookId;

    @NotNull(message = "독서 시작일을 입력해 주세요.")
    @PastOrPresent(message = "유효하지 않은 날짜입니다.<br/>오늘 또는 오늘 이전의 날짜를 선택해 주세요.")
    private final LocalDate startDate;

    // NOTE: 완독일의 경우, 상황에 따라 검증이 달라지므로 서비스 레이어에서 진행
    private final LocalDate endDate;
}
