package com.readingtracker.boochive.enums;

import lombok.Getter;

@Getter
public enum ResourceName {
    REVIEW("리뷰"),
    COLLECTION("컬렉션"),
    PURCHASE_HISTORY("구매 이력"),
    RENTAL_HISTORY("대여 이력"),
    READING_BOOK("독서 정보"),
    READING_RECORD("독서 이력"),
    USER_CONFIG("사용자 설정"),
    READING_NOTE("독서 노트"),
    DEFAULT("데이터");

    private final String name;

    ResourceName(String name) {
        this.name = name;
    }

    public static ResourceName fromClassName(String className) {
        return switch (className) {
            case "Review" -> REVIEW;
            case "BookCollection" -> COLLECTION;
            case "PurchaseHistory" -> PURCHASE_HISTORY;
            case "RentalHistory" -> RENTAL_HISTORY;
            case "ReadingBook" -> READING_BOOK;
            case "ReadingRecord" -> READING_RECORD;
            case "UserConfig" -> USER_CONFIG;
            case "ReadingNote" -> READING_NOTE;
            default -> DEFAULT;
        };
    }
}

