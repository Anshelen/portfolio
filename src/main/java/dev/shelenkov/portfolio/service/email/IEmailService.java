package dev.shelenkov.portfolio.service.email;

import dev.shelenkov.portfolio.domain.VerificationToken;

import java.io.IOException;

public interface IEmailService {

    void sendConfirmationEmail(VerificationToken token) throws IOException;

    void sendSimpleEmailToAdmin(String name, String subject,
                                String text) throws IOException;
}
