package dev.shelenkov.portfolio.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Date;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private MailProperties mailProperties;

    @Value("spring.mail.username")
    private String from;

    /**
     * Sends a simple email message.
     *
     * @param name name of email sender
     * @param subject email subject
     * @param text email plain content
     */
    public void sendSimpleEmail(String name, String subject, String text) {
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
}
