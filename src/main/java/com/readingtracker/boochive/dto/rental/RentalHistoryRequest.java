package com.readingtracker.boochive.dto.rental;

import com.readingtracker.boochive.config.AppConstants;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class RentalHistoryRequest {

    @NotBlank(message = "도서 정보가" + AppConstants.UNKNOWN_INVALID_ARG_ERROR_MSG)
    @Pattern(regexp = AppConstants.BOOK_ISBN_REGEX, message = "도서 정보가" + AppConstants.UNKNOWN_INVALID_ARG_ERROR_MSG)
    private final String bookIsbn;

    @NotNull(message = "대여일자를 입력해 주세요.")
    @PastOrPresent(message = "유효하지 않은 날짜입니다.<br/>오늘 또는 오늘 이전의 날짜를 선택해 주세요.")
    private final LocalDate rentalDate;

    private final LocalDate returnDate;

    @Size(max = 50, message = "대여처는 최대 50자까지 입력할 수 있습니다.")
    private final String rentalFrom;

    @Size(max = 200, message = "메모는 최대 200자까지 입력할 수 있습니다.")
    private final String memo;
}
