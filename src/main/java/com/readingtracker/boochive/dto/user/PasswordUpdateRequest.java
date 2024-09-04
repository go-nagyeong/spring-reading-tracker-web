package com.readingtracker.boochive.dto.user;

import com.readingtracker.boochive.config.AppConstants;
import com.readingtracker.boochive.validator.ConfirmPassword;
import com.readingtracker.boochive.validator.NewPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NewPassword
@ConfirmPassword
public class PasswordUpdateRequest {

    @NotBlank(message = "현재 비밀번호를 입력해 주세요.")
    private final String originPassword;

    @Setter // 비밀번호 암호화 업데이트
    @NotBlank(message = "새 비밀번호를 입력해 주세요.")
    @Pattern(regexp = AppConstants.PASSWORD_REGEX, message = AppConstants.PASSWORD_REGEX_ERROR_MSG)
    private String password;

    @NotBlank(message = "새 비밀번호 확인을 입력해 주세요.")
    private final String confirmPassword;
}
