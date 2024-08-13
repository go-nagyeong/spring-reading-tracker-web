package com.readingtracker.boochive.exception;

public class ResourceAccessDeniedException extends RuntimeException {
    public ResourceAccessDeniedException(String resourceName) {
        super("해당 " + resourceName + "에 대한 접근 권한이 없습니다.");
    }
}
