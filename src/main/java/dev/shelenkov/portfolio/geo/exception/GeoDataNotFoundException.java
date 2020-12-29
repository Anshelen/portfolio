package dev.shelenkov.portfolio.geo.exception;

/**
 * Exception thrown when provider can not find requested information.
 */
public class GeoDataNotFoundException extends GeoServiceException {

    public GeoDataNotFoundException(String message) {
        super(message);
    }
}
