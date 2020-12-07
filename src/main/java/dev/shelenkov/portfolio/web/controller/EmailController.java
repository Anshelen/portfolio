package dev.shelenkov.portfolio.web.controller;

import dev.shelenkov.portfolio.mail.EmailService;
import dev.shelenkov.portfolio.service.attempts.ISendEmailToAdminAttemptsAware;
import dev.shelenkov.portfolio.web.request.SendEmailRequest;
import dev.shelenkov.portfolio.web.response.ServerErrorResponse;
import dev.shelenkov.portfolio.web.support.ip.Ip;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EmailController {

    private final EmailService emailService;
    private final ISendEmailToAdminAttemptsAware sendEmailToAdminAttemptsAwareService;

    @SuppressWarnings("FeatureEnvy")
    @PostMapping("/email/send")
    public ResponseEntity<Void> sendEmail(@Valid @RequestBody SendEmailRequest sendEmailRequest,
                                          @Ip String ip)
        throws IOException {

        if (sendEmailToAdminAttemptsAwareService.areTooManyEmailsToAdminSent(ip)) {
            log.warn("Too much attempts to send email to admin. Blocked ip: {}", ip);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        emailService.sendSimpleEmailToAdmin(
            sendEmailRequest.getName(), sendEmailRequest.getSubject(), sendEmailRequest.getText());
        sendEmailToAdminAttemptsAwareService.registerEmailToAdminSent(ip);

        return ResponseEntity.ok().build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IOException.class)
    public ServerErrorResponse handleSendingMailError(Exception ex) {
        log.error("Error sending message to admin", ex);
        return new ServerErrorResponse("Can't send e'mail");
    }
}
