package dev.shelenkov.portfolio.service.auxiliary;

public interface IResendConfirmationEmailAttemptsAware {

    void registerConfirmationEmailResent(String ip);

    boolean areTooManyConfirmationEmailsResent(String ip);
}
