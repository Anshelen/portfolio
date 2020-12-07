package dev.shelenkov.portfolio.publisher;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.domain.RegistrationMethod;
import dev.shelenkov.portfolio.event.AccountRegisteredEvent;
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
        Long accountId = account.getId();
        Validate.notNull(accountId);
        Validate.notNull(registrationMethod);
        eventPublisher.publishEvent(
            new AccountRegisteredEvent(this, accountId, registrationMethod));
    }
}
