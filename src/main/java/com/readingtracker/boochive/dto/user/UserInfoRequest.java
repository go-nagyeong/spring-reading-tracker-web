package com.readingtracker.boochive.dto.user;

import com.readingtracker.boochive.config.AppConstants;
import com.readingtracker.boochive.validator.OneOf;
import com.readingtracker.boochive.validator.ValidDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoRequest {

    @NotBlank(message = "이름을 입력해 주세요.")
    @Size(min = 2, max = 10, message = "이름은 2~10자 이내로 입력하셔야 합니다.")
    private final String username;

    private final String profileImage;

    @OneOf(range = {0,1,2,9}, message = "성별 " + AppConstants.UNKNOWN_INVALID_ARG_ERROR_MSG)
    private final Integer sex;

    @ValidDate
    private final String birthdate;

    @Pattern(regexp = AppConstants.PHONE_NUMBER_REGEX, message = "휴대폰번호 형식이 올바르지 않습니다.")
    private final String phoneNumber;
}
