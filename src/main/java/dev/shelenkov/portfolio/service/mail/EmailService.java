package dev.shelenkov.portfolio.service.mail;

import dev.shelenkov.portfolio.model.VerificationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.util.Date;

/**
 * Service for sending email messages.
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final MailProperties mailProperties;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${application.root-url}")
    private String rootUrl;

    /**
     * Sends a simple email message to admin.
     *
     * @param name name of email sender
     * @param subject email subject
     * @param text email plain content
     */
    public void sendSimpleEmailToAdmin(String name, String subject, String text) {
        MimeMessagePreparator messagePreparator = (MimeMessage mimeMessage) -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setFrom(from);
            helper.setTo(mailProperties.getAddressee());
            helper.setSubject(subject);
            helper.setText(String.format("%s пишет: %s", name, text));
            helper.setSentDate(new Date());
        };
        javaMailSender.send(messagePreparator);
    }

    /**
     * Sends an email to a user-registered email address with a link (composed
     * from token) to confirm ownership of this address.
     *
     * @param token verification token to prove email accessory for a user
     */
    public void sendConfirmationEmail(VerificationToken token) {
        MimeMessagePreparator messagePreparator = (MimeMessage mimeMessage) -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setFrom(from);
            helper.setTo(token.getAccount().getEmail());
            helper.setSubject("Подтверждение регистрации");

            Context context = new Context();
            context.setVariable("webVersion", false);
            context.setVariable("rootUrl", rootUrl);
            context.setVariable("token", token.getToken());
            String content = templateEngine.process("email/verifyEmailAddress.html", context);

            helper.setText(content, true);
            helper.setSentDate(new Date());
        };
        javaMailSender.send(messagePreparator);
    }
}
