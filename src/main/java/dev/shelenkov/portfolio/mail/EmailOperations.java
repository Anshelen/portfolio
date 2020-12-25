package dev.shelenkov.portfolio.mail;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.domain.Country;
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

    /**
     * Sends email to notify user that login attempt from suspicious location was made.
     *
     * @param account user account
     * @param ip      IP address of suspicious login attempt
     * @param country IP address country
     * @throws IOException in case of send email errors
     */
    void sendSuspiciousLocationEmail(Account account, String ip,
                                     Country country) throws IOException;
}
