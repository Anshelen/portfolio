package dev.shelenkov.portfolio.service.attempts;

public interface LoginAttemptsAware {

    void registerSuccessfulLogin(String ip);

    void registerFailedLogin(String ip);

    boolean areTooManyFailedLoginAttempts(String ip);
}
