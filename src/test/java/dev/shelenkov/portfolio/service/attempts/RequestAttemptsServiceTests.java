package dev.shelenkov.portfolio.service.attempts;

import dev.shelenkov.portfolio.service.config.MaxAttemptsProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestAttemptsServiceTests {

    private RequestAttemptsService service;

    @BeforeEach
    public void init() {
        MaxAttemptsProperties maxAttemptsProperties
            = new MaxAttemptsProperties(3, 3, 3);
        service = new RequestAttemptsService(maxAttemptsProperties);
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

    @Test
    public void areTooManyFailedLoginAttempts_noAttempts_false() {
        boolean result = service.areTooManyFailedLoginAttempts("192.168.1.1");
        assertThat(result).isFalse();
    }

    @Test
    public void areTooManyFailedLoginAttempts_tooManyFailedAttemptsInARow_true() {
        service.registerFailedLogin("192.168.1.1");
        service.registerFailedLogin("192.168.1.1");
        service.registerFailedLogin("192.168.1.1");
        boolean result = service.areTooManyFailedLoginAttempts("192.168.1.1");
        assertThat(result).isTrue();
    }

    @Test
    public void areTooManyFailedLoginAttempts_manyFailedAttemptsButNotInARow_false() {
        service.registerFailedLogin("192.168.1.1");
        service.registerFailedLogin("192.168.1.1");
        service.registerSuccessfulLogin("192.168.1.1");
        service.registerFailedLogin("192.168.1.1");
        boolean result = service.areTooManyFailedLoginAttempts("192.168.1.1");
        assertThat(result).isFalse();
    }

    @Test
    public void areTooManyEmailsToAdminSent_noEmailsToAdminSent_false() {
        boolean result = service.areTooManyEmailsToAdminSent("192.168.1.1");
        assertThat(result).isFalse();
    }

    @Test
    public void areTooManyEmailsToAdminSent_tooManyEmailsToAdminSent_true() {
        service.registerEmailToAdminSent("192.168.1.1");
        service.registerEmailToAdminSent("192.168.1.1");
        service.registerEmailToAdminSent("192.168.1.1");
        boolean result = service.areTooManyEmailsToAdminSent("192.168.1.1");
        assertThat(result).isTrue();
    }
}
