package com.readingtracker.boochive.dto;

import com.readingtracker.boochive.config.AppConstants;
import com.readingtracker.boochive.enums.ReadingStatus;
import com.readingtracker.boochive.validator.ValidEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadingBookRequest {

    @NotBlank(message = "ISBN " + AppConstants.UNKNOWN_INVALID_ARG_ERROR_MSG)
    @Pattern(regexp = AppConstants.BOOK_ISBN_REGEX, message = "ISBN " + AppConstants.UNKNOWN_INVALID_ARG_ERROR_MSG)
    private final String bookIsbn;

    private final Long collectionId;

    @NotBlank(message = "독서 상태 " + AppConstants.UNKNOWN_INVALID_ARG_ERROR_MSG)
    @ValidEnum(enumClass = ReadingStatus.class, message = "독서 상태 " + AppConstants.UNKNOWN_INVALID_ARG_ERROR_MSG)
    private final String readingStatus;
}
