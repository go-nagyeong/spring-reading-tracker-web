package com.readingtracker.boochive.dto;

import jakarta.validation.constraints.*;

public class RegisterForm {

    @NotBlank(message = "이메일을 입력해 주세요.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getIsTermsAgreed() {
        return isTermsAgreed;
    }

    public void setIsTermsAgreed(Boolean termsAgreed) {
        isTermsAgreed = termsAgreed;
    }
}