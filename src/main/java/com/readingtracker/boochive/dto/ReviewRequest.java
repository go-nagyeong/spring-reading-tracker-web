package com.readingtracker.boochive.dto;

import com.readingtracker.boochive.config.AppConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewRequest {

    private final String bookIsbn;

    @Max(value = 10, message = "평점 " + AppConstants.UNKNOWN_INVALID_ARG_ERROR_MSG)
    @Min(value = 0, message = "평점 " + AppConstants.UNKNOWN_INVALID_ARG_ERROR_MSG)
    private final Integer rating;

    @NotBlank(message = "리뷰 내용을 입력해 주세요.")
    private final String reviewText;
}
