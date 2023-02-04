package com.skytel.pigeon.exceptions;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(@NotBlank @Size(min = 1) final String message) {

        super(message);
    }
}
