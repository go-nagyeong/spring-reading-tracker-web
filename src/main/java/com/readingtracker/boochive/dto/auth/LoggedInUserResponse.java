package com.readingtracker.boochive.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoggedInUserResponse {

    private final String email;

    private final String username;

    private final String profileImage;
}
