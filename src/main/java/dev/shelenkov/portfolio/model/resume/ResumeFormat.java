package dev.shelenkov.portfolio.model.resume;

import java.util.Arrays;

/**
 * Format of resume file.
 */
public enum ResumeFormat {

    PDF("pdf"),
    DOCX("docx");

    private final String extension;

    ResumeFormat(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    /**
     * Returns format of resume by its extension. Case is ignored.
     *
     * @param extension resume extension
     * @return Enum instance for specified extension
     */
    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
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
