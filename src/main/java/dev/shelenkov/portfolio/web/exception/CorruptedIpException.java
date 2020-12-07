package dev.shelenkov.portfolio.web.exception;

/**
 * Exception for cases when corrupted ip detected. It can happen when illegal value is transferred
 * via 'X-Forwarded-For' header.
 */
public class CorruptedIpException extends RuntimeException {

    public CorruptedIpException(String ip) {
        super("Corrupted ip detected: " + ip);
    }
}
