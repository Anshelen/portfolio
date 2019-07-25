package dev.shelenkov.portfolio.web.controller;

import dev.shelenkov.portfolio.service.mail.EmailService;
import dev.shelenkov.portfolio.web.wrappers.dto.EmailDTO;
import dev.shelenkov.portfolio.web.wrappers.error.ServerErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    @SuppressWarnings("FeatureEnvy")
    @PostMapping("/email/send")
    public ResponseEntity<Void> sendEmail(@Valid @RequestBody EmailDTO emailDTO) {
        emailService.sendSimpleEmail(
            emailDTO.getName(), emailDTO.getSubject(), emailDTO.getText());
        return ResponseEntity.ok().build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(MailSendException.class)
    public ServerErrorResponse handleSendingMailError() {
        return new ServerErrorResponse("Can't send e'mail");
    }
}
