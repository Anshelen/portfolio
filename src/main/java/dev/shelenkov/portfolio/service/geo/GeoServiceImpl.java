package dev.shelenkov.portfolio.service.geo;

import dev.shelenkov.portfolio.domain.Country;
import dev.shelenkov.portfolio.geo.GeoOperations;
import dev.shelenkov.portfolio.geo.exception.GeoServiceException;
import dev.shelenkov.portfolio.repository.CountryRepository;
import dev.shelenkov.portfolio.service.exception.GeoDataUnavailableException;
import dev.shelenkov.portfolio.support.dto.CountryGeoData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeoServiceImpl implements GeoService {

    private final GeoOperations geoOperations;
    private final CountryRepository countryRepository;

    @Transactional
    @Override
    public Country getCountryByIp(String ip) throws GeoDataUnavailableException {
        try {
            CountryGeoData result = geoOperations.getGeoData(ip);
            String countryCode = result.getCode();

            Optional<Country> countryOpt = countryRepository.getByCode(countryCode);
            return countryOpt.orElseGet(() -> {
                Country country = new Country(countryCode, result.getName());
                countryRepository.save(country);
                return country;
            });
        } catch (GeoServiceException e) {
            throw new GeoDataUnavailableException(e);
        }
    }
}
