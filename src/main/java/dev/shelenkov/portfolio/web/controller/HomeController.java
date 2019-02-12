package dev.shelenkov.portfolio.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;

@Controller
public class HomeController {

    private static final String HOME_VIEW_NAME = "index";

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Principal principal) {
        return HOME_VIEW_NAME;
    }
}
