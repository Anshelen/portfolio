package dev.shelenkov.portfolio.service.resume;

import dev.shelenkov.portfolio.domain.resume.ResumeFormat;
import dev.shelenkov.portfolio.domain.resume.ResumeLanguage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
public class ResumeResourceFactory {

    @Autowired
    private ResourceLoader resourceLoader;

    /**
     * Returns a resume resource for provided language and format.
     *
     * @param language resume language
     * @param format   resume format
     * @return resume resource
     */
    public Resource getResumeResource(ResumeLanguage language,
                                      ResumeFormat format) {
        String fileName = String.format("classpath:resume/shelenkov_%s.%s",
            language.getAbbreviation(), format.getExtension());
        return resourceLoader.getResource(fileName);
    }
}
