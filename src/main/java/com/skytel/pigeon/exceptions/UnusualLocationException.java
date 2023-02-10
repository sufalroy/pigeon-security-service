package com.skytel.pigeon.exceptions;

import org.springframework.security.core.AuthenticationException;

public final class UnusualLocationException extends AuthenticationException {

    public UnusualLocationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UnusualLocationException(final String message) {
        super(message);
    }
}
