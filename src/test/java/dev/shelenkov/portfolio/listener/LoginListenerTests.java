package dev.shelenkov.portfolio.listener;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.domain.Country;
import dev.shelenkov.portfolio.event.LoginEvent;
import dev.shelenkov.portfolio.publisher.EventsPublisher;
import dev.shelenkov.portfolio.service.account.AccountService;
import dev.shelenkov.portfolio.service.exception.GeoDataUnavailableException;
import dev.shelenkov.portfolio.service.geo.GeoService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginListenerTests {

    @Mock
    private GeoService geoService;

    @Mock
    private AccountService accountService;

    @Mock
    private EventsPublisher eventsPublisher;

    private LoginListener loginListener;

    @BeforeEach
    public void setUp() {
        loginListener = new LoginListener(geoService, accountService, eventsPublisher);
    }

    @Test
    public void onLoginEvent_geoDataNotAvailable_noEventFired() {
        expectGeoDataUnavailableForIp("77.88.11.22");
        loginListener.onLoginEvent(new LoginEvent(1L, "77.88.11.22"));
        assertNoSuspiciousLocationLoginEventFired();
    }

    @Test
    public void onLoginEvent_accountNotExist_InvalidStateException() {
        expectGeoDataForIp("77.88.11.22", country(1L, "RU", "Russia"));

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() ->
            loginListener.onLoginEvent(new LoginEvent(1L, "77.88.11.22")));
        assertNoSuspiciousLocationLoginEventFired();
    }

    @Test
    public void onLoginEvent_noCountriesForAccount_addedNewCountryToAccountAndNoEventFired() {
        Account account = accountWithoutCountries(1L);
        Country country = country(1L, "RU", "Russia");

        expectGeoDataForIp("77.88.11.22", country);
        expectAccountForId(1L, account);

        loginListener.onLoginEvent(new LoginEvent(1L, "77.88.11.22"));

        assertAccountHasCountries(account, country);
        assertNoSuspiciousLocationLoginEventFired();
    }

    @Test
    public void onLoginEvent_countryExistsForAccount_noEventFired() {
        Country country = country(1L, "RU", "Russia");
        Account account = account(1L, country);

        expectGeoDataForIp("77.88.11.22", country);
        expectAccountForId(1L, account);

        loginListener.onLoginEvent(new LoginEvent(1L, "77.88.11.22"));

        assertAccountHasCountries(account, country);
        assertNoSuspiciousLocationLoginEventFired();
    }

    @Test
    public void onLoginEvent_newCountryForAccount_addedNewCountryToAccountAndEventFired() {
        Country country = country(1L, "RU", "Russia");
        Country newCountry = country(2L, "US", "USA");
        Account account = account(1L, country);

        expectGeoDataForIp("77.88.11.22", newCountry);
        expectAccountForId(1L, account);

        loginListener.onLoginEvent(new LoginEvent(1L, "77.88.11.22"));

        assertAccountHasCountries(account, country, newCountry);
        assertSuspiciousLocationEventFired(account, "77.88.11.22", newCountry);
    }

    @SneakyThrows
    private void expectGeoDataUnavailableForIp(String ip) {
        when(geoService.getCountryByIp(ip)).thenThrow(GeoDataUnavailableException.class);
    }

    @SneakyThrows
    private void expectGeoDataForIp(String ip, Country country) {
        when(geoService.getCountryByIp(ip)).thenReturn(country);
    }

    private void expectAccountForId(long id, Account account) {
        when(accountService.getByIdWithCountries(id)).thenReturn(account);
    }

    private void assertNoSuspiciousLocationLoginEventFired() {
        verify(eventsPublisher, never()).suspiciousLocationLogin(any(), anyString(), any());
    }

    private void assertAccountHasCountries(Account account, Country... countries) {
        assertThat(account.getLoginCountries()).containsExactlyInAnyOrder(countries);
    }

    private void assertSuspiciousLocationEventFired(Account account, String ip, Country country) {
        verify(eventsPublisher).suspiciousLocationLogin(account, ip, country);
    }

    private static Country country(long id, String code, String name) {
        Country country = new Country(code, name);
        country.setId(id);
        return country;
    }

    private static Account account(long id, Country... countries) {
        Account account = new Account();
        account.setId(id);
        Arrays.stream(countries).forEach(account::addLoginCountry);
        return account;
    }

    private static Account accountWithoutCountries(long id) {
        return account(id);
    }
}
