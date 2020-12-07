package dev.shelenkov.portfolio.web.support.converters;

import dev.shelenkov.portfolio.domain.resume.ResumeLanguage;
import org.springframework.core.convert.converter.Converter;

public class ResumeLanguageConverter implements Converter<String, ResumeLanguage> {

    @Override
    public ResumeLanguage convert(String source) {
        return ResumeLanguage.fromAbbreviation(source);
    }
}
