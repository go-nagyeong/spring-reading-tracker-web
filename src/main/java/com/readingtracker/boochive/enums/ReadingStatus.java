package com.readingtracker.boochive.enums;

import lombok.Getter;

@Getter
public enum ReadingStatus {
    // 이름, 우선 순위 (정렬 순서)
    TO_READ("읽을 예정", 3),
    READING("읽는 중", 1),
    READ("읽음", 2),
    PAUSED("잠시 중단", 4),
    ABANDONED("독서 포기", 5);

    private final String name;
    private final Integer priority;

    ReadingStatus(String name, Integer priority) {
        this.name = name;
        this.priority = priority;
    }
}
