package dev.shelenkov.portfolio.service.email;

import dev.shelenkov.portfolio.domain.VerificationToken;
import dev.shelenkov.portfolio.mail.EmailOperations;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final EmailOperations emailOperations;

    @Override
    public void sendConfirmationEmail(VerificationToken token) throws IOException {
        emailOperations.sendConfirmationEmail(token);
    }

    @Override
    public void sendSimpleEmailToAdmin(String name, String subject,
                                       String text) throws IOException {

        emailOperations.sendSimpleEmailToAdmin(name, subject, text);
    }
}
