package dev.shelenkov.portfolio.geo;

import dev.shelenkov.portfolio.geo.exception.GeoServiceException;
import dev.shelenkov.portfolio.support.dto.CountryGeoData;

public interface GeoOperations {

    /**
     * Fetches country data for the provided IP address.
     *
     * @param ip IPv4 address
     * @return data of the country this IP belongs to
     * @throws GeoServiceException if the data can not be retrieved
     */
    CountryGeoData getGeoData(String ip) throws GeoServiceException;
}
