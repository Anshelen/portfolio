package dev.shelenkov.portfolio.service.email;

import dev.shelenkov.portfolio.domain.VerificationToken;
import dev.shelenkov.portfolio.mail.SendGridService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {

    private final SendGridService sendGridService;

    @Override
    public void sendConfirmationEmail(VerificationToken token) throws IOException {
        sendGridService.sendConfirmationEmail(token);
    }

    @Override
    public void sendSimpleEmailToAdmin(String name, String subject,
                                       String text) throws IOException {

        sendGridService.sendSimpleEmailToAdmin(name, subject, text);
    }
}
