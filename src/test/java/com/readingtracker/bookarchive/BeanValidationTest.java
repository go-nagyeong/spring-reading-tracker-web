package com.readingtracker.bookarchive;

import com.readingtracker.boochive.dto.RegisterForm;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

public class BeanValidationTest {

    @Test
    void registerFormValidators() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        RegisterForm form = new RegisterForm();
        form.setEmail("test");

        Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);
        for (ConstraintViolation<RegisterForm> violation : violations) {
            System.out.println("violation = " + violation);
            System.out.println("violation.getMessage() = " + violation.getMessage());
        }
    }
}
