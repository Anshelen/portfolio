package dev.shelenkov.portfolio.service.email;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.domain.Country;
import dev.shelenkov.portfolio.domain.VerificationToken;

import java.io.IOException;

public interface EmailService {

    void sendConfirmationEmail(VerificationToken token) throws IOException;

    void sendSimpleEmailToAdmin(String name, String subject,
                                String text) throws IOException;

    void sendSuspiciousLocationEmail(Account account, String ip,
                                     Country country) throws IOException;
}
