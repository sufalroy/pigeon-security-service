package com.skytel.pigeon.validators;

import com.skytel.pigeon.web.requests.RegisterRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(final PasswordMatches constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(final Object o,
            final ConstraintValidatorContext context) {

        final RegisterRequest request = (RegisterRequest) o;

        return request.getPassword().equals(request.getMatchingPassword());
    }
}
