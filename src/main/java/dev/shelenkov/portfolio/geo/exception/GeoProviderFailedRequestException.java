package dev.shelenkov.portfolio.geo.exception;

/**
 * Exception thrown when request to geo provider fails.
 */
public class GeoProviderFailedRequestException extends GeoServiceException {

    public GeoProviderFailedRequestException(String message) {
        super(message);
    }

    public GeoProviderFailedRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
