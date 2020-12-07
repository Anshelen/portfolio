package dev.shelenkov.portfolio.security.exception;

import org.springframework.security.core.AuthenticationException;

public class TooManyLoginAttemptsException extends AuthenticationException {

    public TooManyLoginAttemptsException(String ip) {
        super("Too many attempts for ip: " + ip);
    }
}
