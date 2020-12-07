package dev.shelenkov.portfolio.service.attempts;

public interface ResendConfirmationEmailAttemptsAware {

    void registerConfirmationEmailResent(String ip);

    boolean areTooManyConfirmationEmailsResent(String ip);
}
