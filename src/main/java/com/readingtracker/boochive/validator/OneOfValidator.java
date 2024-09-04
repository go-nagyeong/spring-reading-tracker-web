package com.readingtracker.boochive.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class OneOfValidator implements ConstraintValidator<OneOf, Integer> {

    private Set<Integer> range;

    @Override
    public void initialize(OneOf constraintAnnotation) {
        this.range = Arrays.stream(constraintAnnotation.range()).boxed().collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return range.contains(value);
    }
}
