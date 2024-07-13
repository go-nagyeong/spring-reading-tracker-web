package com.readingtracker.boochive.domain;

import lombok.Getter;

@Getter
public enum ReadingStatus {
    TO_READ("읽을 예정"),
    READING("읽는 중"),
    READ("읽음"),
    PAUSED("잠시 중단"),
    ABANDONED("독서 포기");

    private final String name;

    ReadingStatus(String name) {
        this.name = name;
    }
}
