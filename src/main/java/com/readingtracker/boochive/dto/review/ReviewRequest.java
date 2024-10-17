package com.readingtracker.boochive.dto.review;

import com.readingtracker.boochive.config.AppConstants;
import com.readingtracker.boochive.validator.PlainTextLength;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewRequest {

    @NotBlank(message = "도서 정보가" + AppConstants.UNKNOWN_INVALID_ARG_ERROR_MSG)
    @Pattern(regexp = AppConstants.BOOK_ISBN_REGEX, message = "도서 정보가" + AppConstants.UNKNOWN_INVALID_ARG_ERROR_MSG)
    private final String bookIsbn;

    @Max(value = 10, message = "평점이" + AppConstants.UNKNOWN_INVALID_ARG_ERROR_MSG)
    @Min(value = 0, message = "평점이" + AppConstants.UNKNOWN_INVALID_ARG_ERROR_MSG)
    private final Integer rating;

    @NotEmpty(message = "리뷰 내용을 입력해 주세요.")
    @PlainTextLength(min = 2, max = 500, message = "리뷰는 2~500자 이내로 입력하셔야 합니다.")
    private final String reviewText;
}
