package dev.shelenkov.portfolio.web.wrappers.error;

import lombok.Value;

@Value
public class ServerErrorResponse {
    private final String message;
}
