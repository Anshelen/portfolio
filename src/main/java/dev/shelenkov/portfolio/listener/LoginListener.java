package dev.shelenkov.portfolio.listener;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.domain.Country;
import dev.shelenkov.portfolio.event.LoginEvent;
import dev.shelenkov.portfolio.publisher.EventsPublisher;
import dev.shelenkov.portfolio.service.account.AccountService;
import dev.shelenkov.portfolio.service.exception.GeoDataUnavailableException;
import dev.shelenkov.portfolio.service.geo.GeoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Component
@RequiredArgsConstructor
@Validated
@Slf4j
public class LoginListener {

    private final GeoService geoService;
    private final AccountService accountService;
    private final EventsPublisher eventsPublisher;

    @SuppressWarnings("FeatureEnvy")
    @Async
    @EventListener
    @Transactional
    public void onLoginEvent(@Valid LoginEvent event) {

        log.debug("Processing login event: {}", event);

        String ip = event.getIp();
        long accountId = event.getAccountId();

        try {
            Country country = geoService.getCountryByIp(ip);
            Account account = accountService.getByIdWithCountries(accountId);

            Validate.validState(account != null);

            boolean isNewLoginCountry = !account.containsLoginCountry(country);
            boolean hasAnyLoginCountries = !account.getLoginCountries().isEmpty();

            if (hasAnyLoginCountries && isNewLoginCountry) {
                eventsPublisher.suspiciousLocationLogin(account, ip, country);
            }

            if (isNewLoginCountry) {
                account.addLoginCountry(country);
            }
        } catch (GeoDataUnavailableException e) {
            log.error("Can not receive geo data", e);
        }
    }
}
