package com.readingtracker.boochive.validator;

import com.readingtracker.boochive.dto.user.PasswordUpdateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NewPasswordValidator implements ConstraintValidator<NewPassword, PasswordUpdateRequest> {

    @Override
    public void initialize(NewPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(PasswordUpdateRequest request, ConstraintValidatorContext constraintValidatorContext) {
        return request.getPassword() != null && !request.getPassword().equals(request.getOriginPassword());
    }
}
