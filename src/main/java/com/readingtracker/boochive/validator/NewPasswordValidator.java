package com.readingtracker.boochive.validator;

import com.readingtracker.boochive.dto.PasswordDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NewPasswordValidator implements ConstraintValidator<NewPassword, PasswordDto> {

    @Override
    public void initialize(NewPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(PasswordDto passwordDto, ConstraintValidatorContext constraintValidatorContext) {
        return passwordDto.getPassword() != null && !passwordDto.getPassword().equals(passwordDto.getOriginPassword());
    }
}
