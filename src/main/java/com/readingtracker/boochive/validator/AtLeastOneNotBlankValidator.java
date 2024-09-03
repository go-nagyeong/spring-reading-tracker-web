package com.readingtracker.boochive.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

public class AtLeastOneNotBlankValidator implements ConstraintValidator<AtLeastOneNotBlank, Object> {

    private String[] fields;

    @Override
    public void initialize(AtLeastOneNotBlank constraintAnnotation) {
        this.fields = constraintAnnotation.fields();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        try {
            for (String fieldName : fields) {
                Field field = value.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);

                String fieldValue = (String) field.get(value);

                if (StringUtils.isNotBlank(fieldValue)) {
                    return true; // 하나의 필드라도 비어있지 않으면 true 리턴
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
