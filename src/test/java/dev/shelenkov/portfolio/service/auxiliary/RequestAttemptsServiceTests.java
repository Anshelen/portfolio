package dev.shelenkov.portfolio.service.auxiliary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestAttemptsServiceTests {

    private static final int MAX_RESEND_CONFIRMATION_EMAIL_ATTEMPTS = 3;

    private RequestAttemptsService service;

    @BeforeEach
    public void init() {
        service = new RequestAttemptsService(MAX_RESEND_CONFIRMATION_EMAIL_ATTEMPTS);
    }

    @Test
    public void areTooManyConfirmationEmailsResent_noConfirmationEmailsSent_false() {
        boolean result = service.areTooManyConfirmationEmailsResent("192.168.1.1");
        assertThat(result).isFalse();
    }

    @Test
    public void areTooManyConfirmationEmailsResent_tooManyConfirmationEmailsSent_true() {
        service.registerConfirmationEmailResent("192.168.1.1");
        service.registerConfirmationEmailResent("192.168.1.1");
        service.registerConfirmationEmailResent("192.168.1.1");
        boolean result = service.areTooManyConfirmationEmailsResent("192.168.1.1");
        assertThat(result).isTrue();
    }
}
