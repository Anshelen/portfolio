package dev.shelenkov.portfolio.security;

import dev.shelenkov.portfolio.service.auxiliary.ILoginAttemptsAware;
import org.springframework.lang.NonNull;

public class MockLoginAttemptsAwareService implements ILoginAttemptsAware {

    @Override
    public void registerSuccessfulLogin(@NonNull String ip) {
    }

    @Override
    public void registerFailedLogin(@NonNull String ip) {
    }

    @Override
    public boolean areTooManyFailedLoginAttempts(@NonNull String ip) {
        return false;
    }
}
