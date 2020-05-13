package dev.shelenkov.portfolio.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class EmailWebViewController {

    private final ResourceLoader resourceLoader;

    @Value("${application.root-url}")
    private String rootUrl;

    @GetMapping("/mail/{view:\\w+}")
    public String viewMail(@PathVariable("view") String viewName,
                           @RequestParam Map<String, String> parameters,
                           Model model) {

        Resource mailResource = resourceLoader.getResource(
            String.format("classpath:/templates/email/%s.html", viewName));
        if (mailResource.exists()) {
            model.mergeAttributes(parameters);
            return String.format("email/%s", viewName);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    /*
     * In order not to display "View in browser" link.
     */
    @ModelAttribute
    public void markWebVersion(Model model) {
        model.addAttribute("webVersion", true);
        model.addAttribute("rootUrl", rootUrl);
    }
}
