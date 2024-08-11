package com.readingtracker.boochive.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "이메일을 입력해 주세요.")
    private final String email;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private final String password;
}
