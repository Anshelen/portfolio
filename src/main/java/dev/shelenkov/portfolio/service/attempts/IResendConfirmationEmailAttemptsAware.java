package dev.shelenkov.portfolio.service.attempts;

public interface IResendConfirmationEmailAttemptsAware {

    void registerConfirmationEmailResent(String ip);

    boolean areTooManyConfirmationEmailsResent(String ip);
}
