package dev.shelenkov.portfolio.geo.exception;

/**
 * Base class for exceptions thrown by geo module.
 */
public class GeoServiceException extends Exception {

    public GeoServiceException(String message) {
        super(message);
    }

    public GeoServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
