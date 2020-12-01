package dev.shelenkov.portfolio.service.auxiliary;

import org.springframework.lang.NonNull;

public interface ILoginAttemptsAware {

    void registerSuccessfulLogin(@NonNull String ip);

    void registerFailedLogin(@NonNull String ip);

    boolean areTooManyFailedLoginAttempts(@NonNull String ip);
}
