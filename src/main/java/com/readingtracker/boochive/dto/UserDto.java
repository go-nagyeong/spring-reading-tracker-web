package com.readingtracker.boochive.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserDto {

    private Long id;

    private String email;

    @NotBlank(message = "이름을 입력해 주세요.")
    @Size(min = 2, max = 10, message = "이름은 2~10자 이내로 입력하셔야 합니다.")
    private String username;

    private String profileImage;

    private Integer sex;

    private LocalDate birthdate;

    @Pattern(regexp = "^(01[016789]{1})?[0-9]{3,4}?[0-9]{4}|$",
            message = "휴대폰번호 형식이 올바르지 않습니다.")
    private String phoneNumber;

    public UserDto(Long id, String email, String username, String profileImage, Integer sex, LocalDate birthdate, String phoneNumber) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.profileImage = profileImage;
        this.sex = sex;
        this.birthdate = birthdate;
        this.phoneNumber = phoneNumber;
    }

}
