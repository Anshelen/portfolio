package dev.shelenkov.portfolio.service.mail;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import dev.shelenkov.portfolio.model.Account;
import dev.shelenkov.portfolio.model.Role;
import dev.shelenkov.portfolio.model.VerificationToken;
import org.assertj.core.api.SoftAssertions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.internet.MimeMessage;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EmailService.class, MailProperties.class})
@Import({MailSenderAutoConfiguration.class, ThymeleafAutoConfiguration.class})
@WebAppConfiguration
@TestPropertySource(locations="classpath:/application-test.properties")
public class EmailServiceTests {

    @Autowired
    private EmailService emailService;

    private GreenMail smtpServer;

    @BeforeEach
    public void setUp() {
        smtpServer = new GreenMail(ServerSetupTest.SMTP);
        smtpServer.setUser("username@mail.ru", "secret");
        smtpServer.start();
    }

    @AfterEach
    public void tearDown() {
        smtpServer.stop();
    }

    @Test
    public void sendConfirmationEmail_Ok_Success() throws Exception {
        Account account = createAccount();
        VerificationToken token = createVerificationToken(account);

        emailService.sendConfirmationEmail(account, token);

        MimeMessage[] receivedMessages = smtpServer.getReceivedMessages();
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(receivedMessages).hasSize(1);

        MimeMessage message = receivedMessages[0];
        softly.assertThat(message.getSubject()).isEqualTo("Подтверждение регистрации");
        softly.assertThat(message.getAllRecipients())
            .hasSize(1).allMatch(e -> e.toString().equals(account.getEmail()));
        softly.assertThat(message.getFrom())
            .hasSize(1).allMatch(e -> "username@mail.ru".equals(e.toString()));
        softly.assertThat(message.getContentType()).isEqualTo("text/html;charset=UTF-8");

        Document document = Jsoup.parse(String.valueOf(message.getContent()));
        softly.assertThat(document.getElementById("logo").attr("src"))
            .isEqualTo("http://test.ru/resources/img/logo.png");
        softly.assertThat(document.getElementById("confirmationLink").attr("href"))
            .isEqualTo("http://test.ru/confirmRegistration?token=" + token.getToken());
        softly.assertThat(document.getElementById("loginLink").attr("href"))
            .isEqualTo("http://test.ru/login.html");
        softly.assertThat(document.getElementById("copyright").text())
            .isEqualTo("© 2019 http://test.ru");
        softly.assertAll();
    }

    private Account createAccount() {
        Role userRole = new Role("ROLE_USER");
        return new Account("Anton", "anshelen@yandex.ru",
            "password", userRole);
    }

    private VerificationToken createVerificationToken(Account account) {
        VerificationToken token = new VerificationToken();
        ReflectionTestUtils.setField(token, "token", UUID.randomUUID());
        ReflectionTestUtils.setField(token, "account", account);
        ReflectionTestUtils.setField(token, "creationDate", Instant.now());
        int tokenTTL = (int) ReflectionTestUtils.getField(
            VerificationToken.class, "EXPIRATION_DAYS");
        Instant expirationDate = Instant.now().plus(tokenTTL, ChronoUnit.DAYS);
        ReflectionTestUtils.setField(token, "expirationDate", expirationDate);
        return token;
    }
}
