package com.readingtracker.boochive.exception;

import com.readingtracker.boochive.util.KoreanUtil;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName) {
        super(resourceName + KoreanUtil.getPostposition(resourceName) + " 찾을 수 없습니다.");
    }
}
