package dev.shelenkov.portfolio.web.response;

import lombok.Value;

@Value
public class ServerErrorResponse {

    private final String message;
}
