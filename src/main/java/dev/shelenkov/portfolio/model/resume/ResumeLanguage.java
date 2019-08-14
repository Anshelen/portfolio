package dev.shelenkov.portfolio.model.resume;

import java.util.Arrays;

/**
 * Resume language.
 */
public enum ResumeLanguage {

    ENGLISH("en"),
    RUSSIAN("ru");

    private final String abbreviation;

    ResumeLanguage(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * Returns language of resume by its language abbreviation. Case is ignored.
     *
     * @param abbreviation resume language abbreviation (e.g. 'en')
     * @return Enum instance for specified abbreviation
     */
    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
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
