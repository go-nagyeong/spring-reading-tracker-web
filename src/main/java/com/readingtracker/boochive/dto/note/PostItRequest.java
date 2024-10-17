package com.readingtracker.boochive.dto.note;

import com.readingtracker.boochive.config.AppConstants;
import com.readingtracker.boochive.enums.NoteType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostItRequest {

    private final Long id; // Batch 작업을 위해 각 요청 데이터에 id 값이 들어감 (일반적인 경우에는 URL에 id값 포함으로 필요 X)

    private final NoteType noteType = NoteType.POST_IT;

    @NotNull(message = "도서 정보가" + AppConstants.UNKNOWN_INVALID_ARG_ERROR_MSG)
    private final Long readingBookId;

    @Size(max = 20, message = "포스트잇 내용은 20자 이내로 입력하셔야 합니다.")
    private final String noteText;

    @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
    @Max(value = 5000, message = "페이지 번호는 최대 5000까지 입력할 수 있습니다.")
    private final Integer pageNumber;

    @Pattern(regexp = AppConstants.COLOR_CODE_REGEX, message = "포스트잇 색상이" + AppConstants.UNKNOWN_INVALID_ARG_ERROR_MSG)
    private final String backgroundColor;
}
