package com.readingtracker.boochive.dto.reading;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @NotBlank(message = "도서 정보가" + AppConstants.UNKNOWN_INVALID_ARG_ERROR_MSG)
    @Pattern(regexp = AppConstants.BOOK_ISBN_REGEX, message = "도서 정보가" + AppConstants.UNKNOWN_INVALID_ARG_ERROR_MSG)
    @JsonProperty("bkId")
    private final String bookIsbn;

    @JsonProperty("colId")
    private final Long collectionId;

    @NotBlank(message = "독서 상태가" + AppConstants.UNKNOWN_INVALID_ARG_ERROR_MSG)
    @ValidEnum(enumClass = ReadingStatus.class, message = "독서 상태가" + AppConstants.UNKNOWN_INVALID_ARG_ERROR_MSG)
    @JsonProperty("rdSttus")
    private final String readingStatus;
}
