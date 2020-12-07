package dev.shelenkov.portfolio.service.attempts;

public interface SendEmailToAdminAttemptsAware {

    void registerEmailToAdminSent(String ip);

    boolean areTooManyEmailsToAdminSent(String ip);
}
