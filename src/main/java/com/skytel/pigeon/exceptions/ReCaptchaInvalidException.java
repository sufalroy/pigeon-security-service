package com.skytel.pigeon.exceptions;

public final class ReCaptchaInvalidException extends RuntimeException {

    public ReCaptchaInvalidException(final String message) {
        super(message);
    }
}
