package dev.shelenkov.portfolio.service.auxiliary;

import org.springframework.lang.NonNull;

public interface ISendEmailToAdminAttemptsAware {

    void registerEmailToAdminSent(@NonNull String ip);

    boolean areTooManyEmailsToAdminSent(@NonNull String ip);
}
