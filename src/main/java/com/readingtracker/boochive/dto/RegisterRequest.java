package com.readingtracker.boochive.dto;

import com.readingtracker.boochive.validator.ConfirmPassword;
import com.readingtracker.boochive.validator.NotDuplicateEmail;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfirmPassword
public class RegisterRequest {

    @NotBlank(message = "이메일을 입력해 주세요.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotDuplicateEmail
    private String email;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    @Pattern(regexp = "^(?!((?:[A-Za-z]+)|(?:[~!@#$%^&*()_+=]+)|(?:[0-9]+))$)[A-Za-z\\d~!@#$%^&*()_+=]{8,20}$",
            message = "비밀번호는 8~20자 이내로 영문, 숫자, 특수문자 중 2가지 이상을 조합하셔야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력해 주세요.")
    private String confirmPassword;

    @NotBlank(message = "이름을 입력해 주세요.")
    @Size(min = 2, max = 10, message = "이름은 2~10자 이내로 입력하셔야 합니다.")
    private String username;

    @AssertTrue(message = "회원가입을 완료하려면 이용약관에 동의해야 합니다.")
    private Boolean isTermsAgreed;
}
