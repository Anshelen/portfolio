package dev.shelenkov.portfolio.mail;

import dev.shelenkov.portfolio.domain.VerificationToken;

import java.io.IOException;

public interface EmailOperations {

    /**
     * Sends a simple email message to admin.
     *
     * @param name    name of email sender
     * @param subject email subject
     * @param text    email plain content
     */
    void sendSimpleEmailToAdmin(String name, String subject, String text) throws IOException;

    /**
     * Sends email to user-registered email address with a link (composed
     * from token) to confirm ownership of this address.
     *
     * @param token verification token to prove email accessory for a user
     */
    void sendConfirmationEmail(VerificationToken token) throws IOException;
}
