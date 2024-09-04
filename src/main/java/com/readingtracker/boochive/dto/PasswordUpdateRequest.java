package com.readingtracker.boochive.dto;

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
    @Pattern(regexp = "^(?!((?:[A-Za-z]+)|(?:[~!@#$%^&*()_+=]+)|(?:[0-9]+))$)[A-Za-z\\d~!@#$%^&*()_+=]{8,20}$",
            message = "비밀번호는 8~20자 이내로 영문, 숫자, 특수문자 중 2가지 이상을 조합하셔야 합니다.")
    private String password;

    @NotBlank(message = "새 비밀번호 확인을 입력해 주세요.")
    private final String confirmPassword;
}
