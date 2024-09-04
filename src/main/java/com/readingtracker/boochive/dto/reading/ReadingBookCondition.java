package com.readingtracker.boochive.dto.reading;

import com.readingtracker.boochive.config.AppConstants;
import com.readingtracker.boochive.enums.ReadingStatus;
import com.readingtracker.boochive.validator.ValidEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadingBookCondition {

    @ValidEnum(enumClass = ReadingStatus.class, message = "독서 상태 " + AppConstants.UNKNOWN_INVALID_ARG_ERROR_MSG)
    private final String readingStatus;

    private final Long collectionId;

    private final Boolean isOwned;
}
