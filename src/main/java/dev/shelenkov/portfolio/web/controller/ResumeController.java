package dev.shelenkov.portfolio.web.controller;

import dev.shelenkov.portfolio.model.resume.ResumeFormat;
import dev.shelenkov.portfolio.model.resume.ResumeLanguage;
import dev.shelenkov.portfolio.service.resume.ResumeResourceFactory;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeResourceFactory resumeResourceFactory;

    @SneakyThrows(IOException.class)
    @GetMapping(value = "/resume", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<Resource> downloadResume(@RequestParam ResumeLanguage lang,
                                                   @RequestParam ResumeFormat format) {

        Resource resource = resumeResourceFactory.getResumeResource(lang, format);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(resource.contentLength());
        headers.setContentDisposition(ContentDisposition
            .builder("attachment")
            .filename(Objects.requireNonNull(resource.getFilename()))
            .build());

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}
