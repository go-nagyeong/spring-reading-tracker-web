package com.readingtracker.boochive.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 새 비밀번호와 기존 비밀번호 일치 여부 검증 (일치하지 않아야 함)
 */
@Documented
@Constraint(validatedBy = NewPasswordValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
public @interface NewPassword {
    String message() default "기존 비밀번호와 일치합니다. 다른 비밀번호를 입력해 주세요.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
