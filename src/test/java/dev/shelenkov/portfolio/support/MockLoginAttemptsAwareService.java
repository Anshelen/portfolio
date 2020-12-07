package dev.shelenkov.portfolio.support;

import dev.shelenkov.portfolio.service.attempts.LoginAttemptsAware;
import org.springframework.lang.NonNull;

public class MockLoginAttemptsAwareService implements LoginAttemptsAware {

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
