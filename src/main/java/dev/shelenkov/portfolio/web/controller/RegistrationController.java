package dev.shelenkov.portfolio.web.controller;

import dev.shelenkov.portfolio.model.Account;
import dev.shelenkov.portfolio.service.auxiliary.IResendConfirmationEmailAttemptsAware;
import dev.shelenkov.portfolio.service.registration.IRegistrationService;
import dev.shelenkov.portfolio.service.registration.TokenExpiredException;
import dev.shelenkov.portfolio.service.registration.TokenNotValidException;
import dev.shelenkov.portfolio.web.auxiliary.Ip;
import dev.shelenkov.portfolio.web.wrappers.dto.ResendConfirmationEmailDTO;
import dev.shelenkov.portfolio.web.wrappers.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RegistrationController {

    private final IRegistrationService registrationService;
    private final IResendConfirmationEmailAttemptsAware resendConfirmationEmailAttemptsAwareService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "register";
    }

    @SuppressWarnings("FeatureEnvy")
    @PostMapping("/register")
    public ModelAndView registerUserAccount(
        @ModelAttribute("user") @Valid UserDTO userDTO, BindingResult result) {

        if (result.hasErrors()) {
            return new ModelAndView("register", "user", userDTO);
        } else {
            registrationService.registerNewUser(
                userDTO.getUserName(), userDTO.getEmail(), userDTO.getPassword());
            return new ModelAndView("registrationEmailSent", "user", userDTO);
        }
    }

    @GetMapping("/confirmRegistration")
    public ModelAndView confirmRegistration(@RequestParam("token") UUID token) {
        try {
            Account account = registrationService.confirmRegistration(token);
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
        @Valid @RequestBody ResendConfirmationEmailDTO emailDTO,
        @Ip String ip) {

        String email = emailDTO.getEmail();
        log.debug("Resending confirmation email. Email: {}, ip: {}", email, ip);
        if (resendConfirmationEmailAttemptsAwareService.areTooManyConfirmationEmailsResent(ip)) {
            log.warn("Too much attempts to resend confirmation emails. Blocked ip: {}", ip);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        if (registrationService.isSendConfirmationEmailForbidden(email)) {
            return ResponseEntity.badRequest().build();
        }
        registrationService.sendConfirmationEmail(email);
        resendConfirmationEmailAttemptsAwareService.registerConfirmationEmailResent(ip);
        return ResponseEntity.ok().build();
    }
}
