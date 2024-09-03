package com.readingtracker.boochive.dto;

import com.readingtracker.boochive.enums.NoteType;
import com.readingtracker.boochive.validator.AtLeastOneNotBlank;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@AtLeastOneNotBlank(
        fields = {"noteText", "attachmentImage"},
        message = "노트 내용 또는 첨부 이미지를 입력하셔야 합니다."
)
public class HighlightNoteRequest {

    private final NoteType noteType = NoteType.HIGHLIGHT;

    private final String bookIsbn;

    private final String noteText;

    private final String attachmentImage;

    private final String highlightColor;

    private final Integer pageNumber;

    @NotNull(message = "작성일자를 입력해 주세요.")
    @PastOrPresent(message = "유효하지 않은 날짜입니다.<br/>오늘 또는 오늘 이전의 날짜를 선택해 주세요.")
    private final LocalDate createDate;

    @NotNull(message = "공개여부를 선택해 주세요.")
    @Max(value = 1, message = "공개여부 값이 유효하지 않습니다. 올바르게 설정해 주세요.")
    @Min(value = 0, message = "공개여부 값이 유효하지 않습니다. 올바르게 설정해 주세요.")
    private final Integer isPublic;
}