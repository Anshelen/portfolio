package dev.shelenkov.portfolio.event;

import dev.shelenkov.portfolio.domain.Account;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event thrown after a new user registration.
 */
public class OnRegistrationCompleteEvent extends ApplicationEvent {

    private static final long serialVersionUID = -4197979547727509752L;

    @Getter
    private final Long accountId;

    public OnRegistrationCompleteEvent(Object source, Account account) {
        super(source);
        accountId = account.getId();
    }
}
