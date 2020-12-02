package dev.shelenkov.portfolio.service.auxiliary;

public interface ISendEmailToAdminAttemptsAware {

    void registerEmailToAdminSent(String ip);

    boolean areTooManyEmailsToAdminSent(String ip);
}
