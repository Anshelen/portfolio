package dev.shelenkov.portfolio.service.auxiliary;

import org.springframework.lang.NonNull;

public interface IResendConfirmationEmailAttemptsAware {

    void registerConfirmationEmailResent(@NonNull String ip);

    boolean areTooManyConfirmationEmailsResent(@NonNull String ip);
}
