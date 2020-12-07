package dev.shelenkov.portfolio.event;

import dev.shelenkov.portfolio.domain.RegistrationMethod;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event thrown after a new user registration.
 */
@Getter
public class AccountRegisteredEvent extends ApplicationEvent {

    private static final long serialVersionUID = -4197979547727509752L;

    private final long accountId;
    private final RegistrationMethod registrationMethod;

    public AccountRegisteredEvent(Object source, long accountId,
                                  RegistrationMethod registrationMethod) {
        super(source);
        this.accountId = accountId;
        this.registrationMethod = registrationMethod;
    }
}
