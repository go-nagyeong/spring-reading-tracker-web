package com.readingtracker.boochive.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum QueryType {
    KEYWORD, // 제목 + 저자 (기본값)
    TITLE,
    AUTHOR,
    PUBLISHER;

    @JsonValue
    public String get() {
        String name = name();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
}
