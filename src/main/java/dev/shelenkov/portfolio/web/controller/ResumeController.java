package dev.shelenkov.portfolio.web.controller;

import dev.shelenkov.portfolio.model.resume.ResumeFormat;
import dev.shelenkov.portfolio.model.resume.ResumeLanguage;
import dev.shelenkov.portfolio.service.resume.ResumeResourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ResumeController {

    @Autowired
    private ResumeResourceFactory resumeResourceFactory;

    @GetMapping(value = "/resume", produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseBody
    public ResponseEntity<Resource> downloadResume(@RequestParam ResumeLanguage lang,
                                                   @RequestParam ResumeFormat format) {
        Resource resource = resumeResourceFactory.getResumeResource(lang, format);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + resource.getFilename() + '"').body(resource);
    }
}
