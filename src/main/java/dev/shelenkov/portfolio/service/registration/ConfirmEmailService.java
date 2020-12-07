package dev.shelenkov.portfolio.service.registration;

import java.io.IOException;

public interface ConfirmEmailService {

    /**
     * Sends email with a link to confirm email ownership.
     *
     * @param accountId id of disabled account
     */
    void sendConfirmationEmail(long accountId) throws IOException;

    /**
     * Sends email with a link to confirm email ownership.
     *
     * @param email user's email
     */
    void sendConfirmationEmail(String email) throws IOException;

    /**
     * Checks if a confirmation email can be sent.
     *
     * @param email user's email
     */
    boolean isSendConfirmationEmailForbidden(String email);
}
