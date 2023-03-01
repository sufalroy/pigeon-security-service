package com.skytel.pigeon.exceptions;

public final class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(final String message) {
        super(message);
    }
}
