package dev.shelenkov.portfolio.web.controller;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.service.attempts.ResendConfirmationEmailAttemptsAware;
import dev.shelenkov.portfolio.service.exception.TokenExpiredException;
import dev.shelenkov.portfolio.service.exception.TokenNotValidException;
import dev.shelenkov.portfolio.service.registration.ConfirmEmailService;
import dev.shelenkov.portfolio.service.registration.RegistrationService;
import dev.shelenkov.portfolio.web.request.RegisterUserRequest;
import dev.shelenkov.portfolio.web.request.ResendConfirmationEmailRequest;
import dev.shelenkov.portfolio.web.response.ServerErrorResponse;
import dev.shelenkov.portfolio.web.support.ip.Ip;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RegistrationController {

    private final RegistrationService registrationService;
    private final ConfirmEmailService confirmEmailService;
    private final ResendConfirmationEmailAttemptsAware resendConfirmationEmailAttemptsAwareService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new RegisterUserRequest());
        return "register";
    }

    @SuppressWarnings("FeatureEnvy")
    @PostMapping("/register")
    public ModelAndView registerUserAccount(
        @ModelAttribute("user") @Valid RegisterUserRequest registerUserRequest,
        BindingResult result) {

        if (result.hasErrors()) {
            return new ModelAndView("register", "user", registerUserRequest);
        } else {
            registrationService.registerNewUser(
                registerUserRequest.getUserName(), registerUserRequest.getEmail(), registerUserRequest.getPassword());
            return new ModelAndView("registrationEmailSent", "user", registerUserRequest);
        }
    }

    @GetMapping("/confirmRegistration")
    public ModelAndView confirmRegistration(@RequestParam("token") UUID token, @Ip String ip) {
        try {
            Account account = registrationService.confirmRegistration(token, ip);
            return new ModelAndView("successRegistration",
                "account", account);
        } catch (TokenExpiredException e) {
            return new ModelAndView("badRegister",
                "error", "TokenExpired");
        } catch (TokenNotValidException e) {
            return new ModelAndView("badRegister",
                "error", "InvalidToken");
        }
    }

    @PostMapping(
        value = "/resendRegistrationEmail",
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> resendConfirmationEmail(
        @Valid @RequestBody ResendConfirmationEmailRequest emailDTO,
        @Ip String ip) throws IOException {

        String email = emailDTO.getEmail();
        log.debug("Resending confirmation email. Email: {}, ip: {}", email, ip);
        if (resendConfirmationEmailAttemptsAwareService.areTooManyConfirmationEmailsResent(ip)) {
            log.warn("Too much attempts to resend confirmation emails. Blocked ip: {}", ip);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        if (confirmEmailService.isSendConfirmationEmailForbidden(email)) {
            return ResponseEntity.badRequest().build();
        }
        confirmEmailService.sendConfirmationEmail(email);
        resendConfirmationEmailAttemptsAwareService.registerConfirmationEmailResent(ip);
        return ResponseEntity.ok().build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IOException.class)
    public ServerErrorResponse handleSendConfirmEmailError(IOException ex) {
        log.error("Error sending confirm email message", ex);
        return new ServerErrorResponse("Can't send e'mail");
    }
}
