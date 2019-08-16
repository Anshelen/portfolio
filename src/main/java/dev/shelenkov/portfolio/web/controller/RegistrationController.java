package dev.shelenkov.portfolio.web.controller;

import dev.shelenkov.portfolio.model.Account;
import dev.shelenkov.portfolio.service.registration.RegistrationService;
import dev.shelenkov.portfolio.service.registration.TokenExpiredException;
import dev.shelenkov.portfolio.service.registration.TokenNotValidException;
import dev.shelenkov.portfolio.web.wrappers.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.UUID;

@Controller
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

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
}
