package dev.shelenkov.portfolio.service.geo;

import dev.shelenkov.portfolio.domain.Country;
import dev.shelenkov.portfolio.geo.GeoOperations;
import dev.shelenkov.portfolio.geo.exception.GeoDataNotFoundException;
import dev.shelenkov.portfolio.geo.exception.GeoProviderFailedRequestException;
import dev.shelenkov.portfolio.repository.CountryRepository;
import dev.shelenkov.portfolio.service.exception.GeoDataUnavailableException;
import dev.shelenkov.portfolio.support.dto.CountryGeoData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GeoServiceImplTests {

    @Mock
    private GeoOperations geoOperations;

    @Mock
    private CountryRepository countryRepository;

    private GeoService geoService;

    @BeforeEach
    public void setUp() {
        geoService = new GeoServiceImpl(geoOperations, countryRepository);
    }

    @Test
    public void getCountryByIp_unknownIpForProvider_GeoDataUnavailableException() {
        expectGeoProviderDoesNotHaveInfoAboutIp("77.55.44.11");

        assertThatExceptionOfType(GeoDataUnavailableException.class).isThrownBy(
            () -> geoService.getCountryByIp("77.55.44.11"));
    }

    @Test
    public void getCountryByIp_providerError_GeoDataUnavailableException() {
        expectGeoProviderErrorOccurred("77.55.44.11");

        assertThatExceptionOfType(GeoDataUnavailableException.class).isThrownBy(
            () -> geoService.getCountryByIp("77.55.44.11"));
    }

    @SneakyThrows
    @Test
    public void getCountryByIp_providerReturnsExistingInDBCountry_thatCountryReturned() {
        Country expectedCountry = country(1L, "RU", "Russia");
        expectGeoProviderReturnsData("77.55.44.11", new CountryGeoData("RU", "Russia"));
        expectCountryForCodeExistsInDB("RU", expectedCountry);

        Country actualCountry = geoService.getCountryByIp("77.55.44.11");

        assertThat(actualCountry).isEqualTo(expectedCountry);
    }

    @SneakyThrows
    @Test
    public void getCountryByIp_providerReturnsNotExistingInDBCountry_countrySavedInDBAndReturned() {
        expectGeoProviderReturnsData("77.55.44.11", new CountryGeoData("RU", "Russia"));
        expectCountryForCodeNotExistInDB("RU");
        expectIdIsAssignedWhenCountryIsSavedInDB(1L, new Country("RU", "Russia"));

        Country actualCountry = geoService.getCountryByIp("77.55.44.11");

        assertThat(actualCountry).isEqualTo(country(1L, "RU", "Russia"));
    }

    @SneakyThrows
    private void expectGeoProviderDoesNotHaveInfoAboutIp(String ip) {
        when(geoOperations.getGeoData(ip)).thenThrow(GeoDataNotFoundException.class);
    }

    @SneakyThrows
    private void expectGeoProviderErrorOccurred(String ip) {
        when(geoOperations.getGeoData(ip)).thenThrow(GeoProviderFailedRequestException.class);
    }

    @SneakyThrows
    private void expectGeoProviderReturnsData(String ip, CountryGeoData data) {
        when(geoOperations.getGeoData(ip)).thenReturn(data);
    }

    @SneakyThrows
    private void expectCountryForCodeExistsInDB(String code, Country country) {
        when(countryRepository.getByCode(code)).thenReturn(Optional.of(country));
    }

    @SneakyThrows
    private void expectCountryForCodeNotExistInDB(String code) {
        when(countryRepository.getByCode(code)).thenReturn(Optional.empty());
    }

    @SneakyThrows
    private void expectIdIsAssignedWhenCountryIsSavedInDB(long id, Country country) {
        when(countryRepository.save(country)).thenAnswer(invocation -> {
            Country argument = invocation.getArgument(0, Country.class);
            argument.setId(id);
            return argument;
        });
    }

    private Country country(long id, String code, String name) {
        Country country = new Country(code, name);
        country.setId(id);
        return country;
    }
}
