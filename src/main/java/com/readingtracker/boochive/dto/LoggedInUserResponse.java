package com.readingtracker.boochive.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoggedInUserResponse {

    private final String email;
    private final String name;
    private final String profileImage;
}
