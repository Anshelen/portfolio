package dev.shelenkov.portfolio.web.response;

import lombok.Value;

@Value
public class Violation {

    private final String fieldName;
    private final String message;
}
