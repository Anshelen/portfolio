package dev.shelenkov.portfolio.web.support.converters;

import dev.shelenkov.portfolio.domain.resume.ResumeFormat;
import org.springframework.core.convert.converter.Converter;

public class ResumeFormatConverter implements Converter<String, ResumeFormat> {

    @Override
    public ResumeFormat convert(String source) {
        return ResumeFormat.fromExtension(source);
    }
}
