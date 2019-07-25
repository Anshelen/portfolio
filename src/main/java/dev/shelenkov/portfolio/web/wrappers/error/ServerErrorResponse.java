package dev.shelenkov.portfolio.web.wrappers.error;

public class ServerErrorResponse {

    private final String message;

    public ServerErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
