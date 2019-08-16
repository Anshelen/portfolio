package dev.shelenkov.portfolio.service.registration;

import dev.shelenkov.portfolio.model.Account;
import org.springframework.context.ApplicationEvent;

/**
 * Event thrown after a new user registration.
 */
public class OnRegistrationCompleteEvent extends ApplicationEvent {

    private static final long serialVersionUID = -4197979547727509752L;

    private Long accountId;

    OnRegistrationCompleteEvent(Object source, Account account) {
        super(source);
        accountId = account.getId();
    }

    public Long getAccountId() {
        return accountId;
    }
}
