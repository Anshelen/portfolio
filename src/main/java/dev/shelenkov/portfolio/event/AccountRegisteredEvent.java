package dev.shelenkov.portfolio.event;

import dev.shelenkov.portfolio.domain.RegistrationMethod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Event thrown after a new user registration.
 */
@Getter
@RequiredArgsConstructor
public class AccountRegisteredEvent {

    private final long accountId;
    private final RegistrationMethod registrationMethod;
}
