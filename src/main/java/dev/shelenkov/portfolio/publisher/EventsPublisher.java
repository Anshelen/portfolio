package dev.shelenkov.portfolio.publisher;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.domain.Country;
import dev.shelenkov.portfolio.domain.RegistrationMethod;

public interface EventsPublisher {

    void accountRegistered(Account account, RegistrationMethod registrationMethod);

    void loginCompleted(long accountId, String ip);

    void suspiciousLocationLogin(Account account, String ip, Country country);
}
