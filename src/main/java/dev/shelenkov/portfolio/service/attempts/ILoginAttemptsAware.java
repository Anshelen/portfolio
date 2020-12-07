package dev.shelenkov.portfolio.service.attempts;

public interface ILoginAttemptsAware {

    void registerSuccessfulLogin(String ip);

    void registerFailedLogin(String ip);

    boolean areTooManyFailedLoginAttempts(String ip);
}
