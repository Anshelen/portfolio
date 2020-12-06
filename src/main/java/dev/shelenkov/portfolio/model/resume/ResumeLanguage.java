package dev.shelenkov.portfolio.model.resume;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * Resume language.
 */
@RequiredArgsConstructor
@Getter
public enum ResumeLanguage {

    ENGLISH("en"),
    RUSSIAN("ru");

    private final String abbreviation;

    /**
     * Returns language of resume by its language abbreviation. Case is ignored.
     *
     * @param abbreviation resume language abbreviation (e.g. 'en')
     * @return Enum instance for specified abbreviation
     */
    public static ResumeLanguage fromAbbreviation(String abbreviation) {
        for (ResumeLanguage language : values()) {
            if (language.getAbbreviation().equalsIgnoreCase(abbreviation)) {
                return language;
            }
        }
        throw new IllegalArgumentException(
            "Unknown abbreviation: " + abbreviation
                + ", Allowed values are " + Arrays.toString(values()));
    }
}
