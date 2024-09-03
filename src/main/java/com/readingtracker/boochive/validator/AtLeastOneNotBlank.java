package com.readingtracker.boochive.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 두 필드 중 하나 이상이 값이 있는지 검사
 */
@Documented
@Constraint(validatedBy = AtLeastOneNotBlankValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
public @interface AtLeastOneNotBlank {
    String message() default "필수값을 입력해 주세요.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String[] fields(); // 어떤 필드들을 검사할건지 필드 이름들을 배열로 받기
}
