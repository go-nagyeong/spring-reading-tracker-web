package com.readingtracker.boochive.dto.purchase;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class PurchaseHistoryRequest {

    private final String bookIsbn;

    @NotNull(message = "구매일자를 입력해 주세요.")
    @PastOrPresent(message = "유효하지 않은 날짜입니다.<br/>오늘 또는 오늘 이전의 날짜를 선택해 주세요.")
    private final LocalDate purchaseDate;

    @Size(max = 50, message = "구매처는 최대 50자까지 입력할 수 있습니다.")
    private final String purchaseFrom;

    @DecimalMax(value = "999999999999.99", message = "가격 값이 유효하지 않습니다. 올바르게 설정해 주세요.")
    private final BigDecimal price;

    @Size(max = 200, message = "메모는 최대 200자까지 입력할 수 있습니다.")
    private final String memo;
}
