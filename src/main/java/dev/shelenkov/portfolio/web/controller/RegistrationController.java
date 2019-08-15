package dev.shelenkov.portfolio.web.controller;

import dev.shelenkov.portfolio.service.registration.RegistrationService;
import dev.shelenkov.portfolio.web.wrappers.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

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
            return new ModelAndView("successRegistration", "user", userDTO);
        }
    }
}
