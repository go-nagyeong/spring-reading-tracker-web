package com.readingtracker.boochive.exception;

import lombok.Getter;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
public class CustomArgumentNotValidException extends BindException {
    private final String errorFormat; // 에러 반환 타입 (single/multi)
    private final Map<String, Integer> fieldPriority;
    private final Map<String, Integer> validationPriority;

    private static final Map<String, Integer> VALIDATION_PRIORITY_MAP;

    static {
        Map<String, Integer> map = new HashMap<>();
        map.put("NotNull", 1);
        map.put("NotEmpty", 1);
        map.put("NotBlank", 1);
        map.put("Size", 2);
        map.put("Email", 3);
        map.put("Pattern", 3);
        VALIDATION_PRIORITY_MAP = Collections.unmodifiableMap(map);
    }

    public CustomArgumentNotValidException(String errorFormat, Map<String, Integer> fieldPriority, BindingResult bindingResult) {
        super(bindingResult);
        this.errorFormat = errorFormat;
        this.fieldPriority = fieldPriority;
        this.validationPriority = VALIDATION_PRIORITY_MAP; // 상수로 정의된 불변 맵 사용
    }
}