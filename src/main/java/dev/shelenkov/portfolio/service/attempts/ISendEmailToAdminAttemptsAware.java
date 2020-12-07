package dev.shelenkov.portfolio.service.attempts;

public interface ISendEmailToAdminAttemptsAware {

    void registerEmailToAdminSent(String ip);

    boolean areTooManyEmailsToAdminSent(String ip);
}
