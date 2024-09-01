package com.readingtracker.boochive.enums;

import lombok.Getter;

@Getter
public enum NoteType {
    PENCIL("연필"),
    HIGHLIGHT("형광펜"),
    POST_IT("포스트잇"),
    READING_CARD("독서 카드");

    private final String name;

    NoteType(String name) {
        this.name = name;
    }
}
