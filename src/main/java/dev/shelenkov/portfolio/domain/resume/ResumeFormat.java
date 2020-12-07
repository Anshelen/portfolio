package dev.shelenkov.portfolio.domain.resume;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * Format of resume file.
 */
@RequiredArgsConstructor
@Getter
public enum ResumeFormat {

    PDF("pdf"),
    DOCX("docx");

    private final String extension;

    /**
     * Returns format of resume by its extension. Case is ignored.
     *
     * @param extension resume extension
     * @return Enum instance for specified extension
     */
    public static ResumeFormat fromExtension(String extension) {
        for (ResumeFormat format : values()) {
            if (format.getExtension().equalsIgnoreCase(extension)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unsupported extension: " + extension
            + ", Allowed values are " + Arrays.toString(values()));
    }
}
