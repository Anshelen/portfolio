package dev.shelenkov.portfolio.publisher;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.domain.Country;
import dev.shelenkov.portfolio.domain.RegistrationMethod;
import dev.shelenkov.portfolio.event.AccountRegisteredEvent;
import dev.shelenkov.portfolio.event.LoginEvent;
import dev.shelenkov.portfolio.event.SuspiciousLocationLoginEvent;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventsPublisherImpl implements EventsPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void accountRegistered(Account account, RegistrationMethod registrationMethod) {
        long accountId = Validate.notNull(account.getId());
        eventPublisher.publishEvent(new AccountRegisteredEvent(accountId, registrationMethod));
    }

    @Override
    public void loginCompleted(long accountId, String ip) {
        eventPublisher.publishEvent(new LoginEvent(accountId, ip));
    }

    @Override
    public void suspiciousLocationLogin(Account account, String ip, Country country) {
        eventPublisher.publishEvent(new SuspiciousLocationLoginEvent(account, ip, country));
    }
}
