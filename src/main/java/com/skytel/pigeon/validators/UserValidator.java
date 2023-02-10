package com.skytel.pigeon.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.skytel.pigeon.web.requests.RegisterRequest;


public class UserValidator implements Validator {
    
    @Override
    public boolean supports(final Class<?> clazz) {
        return RegisterRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(final Object obj, final Errors errors) {
        
        
    }
}
