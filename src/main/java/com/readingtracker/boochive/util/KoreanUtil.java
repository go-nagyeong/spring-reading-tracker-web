package com.readingtracker.boochive.util;

public class KoreanUtil {

    /**
     * 한글 음절의 받침 여부를 확인
     */
    public static boolean hasFinalConsonant(char character) {
        // 한글 유니코드 범위에서 받침 유무를 확인합니다.
        return (character >= 0xAC00 && character <= 0xD7A3) && ((character - 0xAC00) % 28 != 0);
    }

    /**
     * 명사의 끝 문자로 적절한 조사를 선택
     */
    public static String getPostposition(String noun) {
        if (noun == null || noun.isEmpty()) {
            return "";
        }

        // 명사의 마지막 문자
        char lastChar = noun.charAt(noun.length() - 1);

        // 받침이 있는 경우 '을', 없는 경우 '를'
        if (hasFinalConsonant(lastChar)) {
            return "을";
        } else {
            return "를";
        }
    }
}
