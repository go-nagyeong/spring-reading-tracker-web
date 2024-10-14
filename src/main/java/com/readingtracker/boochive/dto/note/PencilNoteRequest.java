package com.readingtracker.boochive.dto.note;

import com.readingtracker.boochive.config.AppConstants;
import com.readingtracker.boochive.enums.NoteType;
import com.readingtracker.boochive.validator.PlainTextLength;
import com.readingtracker.boochive.dto.ValidationGroups;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class PencilNoteRequest {

    private final NoteType noteType = NoteType.PENCIL;

    @NotNull(message = "도서 ID " + AppConstants.UNKNOWN_INVALID_ARG_ERROR_MSG, groups = ValidationGroups.Create.class)
    private final Long readingBookId;

    @NotEmpty(message = "본문을 입력해 주세요.")
    @PlainTextLength(min = 2, max = 1500, message = "본문은 2~1500자 이내로 입력하셔야 합니다.")
    private final String noteText;

    @NotNull(message = "작성일자를 입력해 주세요.")
    @PastOrPresent(message = "유효하지 않은 날짜입니다.<br/>오늘 또는 오늘 이전의 날짜를 선택해 주세요.")
    private final LocalDate createDate;

    @NotNull(message = "공개여부를 선택해 주세요.")
    @Max(value = 1, message = "공개여부 값이 유효하지 않습니다. 올바르게 설정해 주세요.")
    @Min(value = 0, message = "공개여부 값이 유효하지 않습니다. 올바르게 설정해 주세요.")
    private final Integer isPublic;
}
