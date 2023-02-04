package com.skytel.pigeon.exceptions;

public final class ReCaptchaUnavailableException extends RuntimeException {

    public ReCaptchaUnavailableException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
