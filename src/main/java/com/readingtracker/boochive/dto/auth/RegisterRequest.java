package com.readingtracker.boochive.dto.auth;

import com.readingtracker.boochive.config.AppConstants;
import com.readingtracker.boochive.validator.ConfirmPassword;
import com.readingtracker.boochive.validator.NotDuplicateEmail;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@ConfirmPassword
public class RegisterRequest {

    @NotBlank(message = "이메일을 입력해 주세요.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotDuplicateEmail
    private final String email;

    @Setter // 비밀번호 암호화 업데이트
    @NotBlank(message = "비밀번호를 입력해 주세요.")
    @Pattern(regexp = AppConstants.PASSWORD_REGEX, message = AppConstants.PASSWORD_REGEX_ERROR_MSG)
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력해 주세요.")
    private final String confirmPassword;

    @NotBlank(message = "이름을 입력해 주세요.")
    @Size(min = 2, max = 10, message = "이름은 2~10자 이내로 입력하셔야 합니다.")
    private final String username;

    @AssertTrue(message = "회원가입을 완료하려면 이용약관에 동의해야 합니다.")
    private final Boolean isTermsAgreed;
}
