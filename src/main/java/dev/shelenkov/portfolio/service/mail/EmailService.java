package dev.shelenkov.portfolio.service.mail;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import dev.shelenkov.portfolio.model.VerificationToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;

/**
 * Service for sending email messages.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final SendGrid sendGrid;
    private final MailProperties mailProperties;
    private final TemplateEngine templateEngine;

    @Value("${application.root-url}")
    private String rootUrl;

    /**
     * Sends a simple email message to admin.
     *
     * @param name    name of email sender
     * @param subject email subject
     * @param text    email plain content
     */
    public void sendSimpleEmailToAdmin(String name, String subject, String text) throws IOException {
        log.debug("Sending email to admin. Name: {}, subject: {}, text: {}",
            name, subject, text);

        Mail mail = getSimpleMailToAdmin(name, subject, text);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = sendGrid.api(request);
        if (!HttpStatus.valueOf(response.getStatusCode()).is2xxSuccessful()) {
            log.error("Error sending email to admin. Response: {}", response.getBody());
        }
    }


    /**
     * Sends an email to a user-registered email address with a link (composed
     * from token) to confirm ownership of this address.
     *
     * @param token verification token to prove email accessory for a user
     */
    public void sendConfirmationEmail(VerificationToken token) throws IOException {
        Long userId = token.getAccount().getId();

        log.debug("Sending confirmation email to user {}", userId);
        Mail mail = getConfirmationMail(token);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = sendGrid.api(request);
        if (!HttpStatus.valueOf(response.getStatusCode()).is2xxSuccessful()) {
            log.error("Error sending confirmation email to user {}. Response: {}",
                token.getAccount().getId(), response.getBody());
        }
    }

    private Mail getSimpleMailToAdmin(String name, String subject, String text) {
        Email fromEmail = new Email(mailProperties.getAdminAddress(), mailProperties.getAdminName());
        Email toEmail = new Email(mailProperties.getAdminAddress());
        String body = String.format("%s пишет: %s", name, text);
        Content content = new Content(MediaType.TEXT_PLAIN_VALUE, body);

        return new Mail(fromEmail, subject, toEmail, content);
    }

    private Mail getConfirmationMail(VerificationToken token) {
        Context context = new Context();
        context.setVariable("webVersion", false);
        context.setVariable("rootUrl", rootUrl);
        context.setVariable("token", token.getToken());
        String body = templateEngine.process("email/verifyEmailAddress.html", context);
        Content content = new Content(MediaType.TEXT_HTML_VALUE, body);

        Email fromEmail = new Email(mailProperties.getAdminAddress(), mailProperties.getAdminName());
        String subject = "Подтверждение регистрации";
        Email toEmail = new Email(token.getAccount().getEmail());
        return new Mail(fromEmail, subject, toEmail, content);
    }
}
