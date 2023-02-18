package com.skytel.pigeon.exceptions;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(@NotBlank @Size(min = 1) final String message) {

        super(message);
    }
}
