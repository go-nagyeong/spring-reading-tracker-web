package com.readingtracker.boochive.validator;

import com.readingtracker.boochive.service.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class NotDuplicateEmailValidator implements ConstraintValidator<NotDuplicateEmail, String> {

    @Autowired
    private UserService userService;

    @Override
    public void initialize(NotDuplicateEmail constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        return userService.findUserByEmailIncludingDeleted(email).isEmpty();
    }
}
