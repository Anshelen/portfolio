package dev.shelenkov.portfolio.service.geo;

import dev.shelenkov.portfolio.domain.Country;
import dev.shelenkov.portfolio.service.exception.GeoDataUnavailableException;

public interface GeoService {

    /**
     * Fetches country entity related to IP address.
     *
     * @param ip IPv4 address
     * @return country for that IP address
     * @throws GeoDataUnavailableException if data can not be retrieved
     */
    Country getCountryByIp(String ip) throws GeoDataUnavailableException;
}
