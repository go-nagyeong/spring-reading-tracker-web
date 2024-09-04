package com.readingtracker.boochive.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class UserInfoResponse {

    private final Long id;

    private final String email;

    private final String username;

    private final String profileImage;

    private final Integer sex;

    private final LocalDate birthdate;

    private final String phoneNumber;
}
