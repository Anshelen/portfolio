package dev.shelenkov.portfolio.publisher;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.domain.RegistrationMethod;

public interface IEventsPublisher {

    void accountRegistered(Account account, RegistrationMethod registrationMethod);
}
