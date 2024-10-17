package com.readingtracker.boochive.config;

public final class AppConstants {

    private AppConstants() {
        // private constructor to prevent instantiation
    }

    /**
     * Regex
     */
    public static final String BOOK_ISBN_REGEX = "^(?=(?:\\D*\\d){10}(?:(?:\\D*\\d){3})?$)[\\d-]+$";
    public static final String PHONE_NUMBER_REGEX = "^(01[016789]{1})?[0-9]{3,4}?[0-9]{4}|$";
    public static final String PASSWORD_REGEX = "^(?!((?:[A-Za-z]+)|(?:[~!@#$%^&*()_+=]+)|(?:[0-9]+))$)[A-Za-z\\d~!@#$%^&*()_+=]{8,20}$";
    public static final String COLOR_CODE_REGEX = "^#([0-9A-Fa-f]{3}|[0-9A-Fa-f]{6})$";

    /**
     * Message
     */
    public static final String PASSWORD_REGEX_ERROR_MSG = "비밀번호는 8~20자 이내로 영문, 숫자, 특수문자 중 2가지 이상을 조합하셔야 합니다.";
    // 사용자의 XSS 공격 예외에 대한 메세지 (임의로 클라이언트 값을 수정한 경우)
    public static final String UNKNOWN_INVALID_ARG_ERROR_MSG = "값이 유효하지 않습니다. 다시 시도해 주세요.";
}
