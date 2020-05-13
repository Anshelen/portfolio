package dev.shelenkov.portfolio.web.wrappers.error;

import lombok.Value;

@Value
public class Violation {
    private final String fieldName;
    private final String message;
}
