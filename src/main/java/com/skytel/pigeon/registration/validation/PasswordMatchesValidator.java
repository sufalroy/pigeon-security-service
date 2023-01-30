package com.skytel.pigeon.registration.validation;

import com.skytel.pigeon.registration.request.RegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(final Object o,
                           final ConstraintValidatorContext context) {

        final RegisterRequest request = (RegisterRequest) o;

        return request.getPassword().equals(request);
    }
}
