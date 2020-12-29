package dev.shelenkov.portfolio.service.exception;

public class GeoDataUnavailableException extends Exception {

    public GeoDataUnavailableException() {
    }

    public GeoDataUnavailableException(Throwable cause) {
        super(cause);
    }

    public GeoDataUnavailableException(String message) {
        super(message);
    }

    public GeoDataUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
