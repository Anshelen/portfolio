package dev.shelenkov.portfolio.event;

import dev.shelenkov.portfolio.domain.RegistrationMethod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * Event thrown after a new user registration.
 */
@Getter
@RequiredArgsConstructor
public class AccountRegisteredEvent {

    @Positive
    private final long accountId;
    @NotNull
    private final RegistrationMethod registrationMethod;
}
