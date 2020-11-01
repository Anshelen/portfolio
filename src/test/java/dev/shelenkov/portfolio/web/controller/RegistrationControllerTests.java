package dev.shelenkov.portfolio.web.controller;

import dev.shelenkov.portfolio.annotations.ConfiguredWebMvcTest;
import dev.shelenkov.portfolio.service.auxiliary.RequestAttemptsService;
import dev.shelenkov.portfolio.service.registration.IRegistrationService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ConfiguredWebMvcTest(RegistrationController.class)
public class RegistrationControllerTests {

    @MockBean
    private IRegistrationService registrationService;

    @MockBean
    private RequestAttemptsService requestAttemptsService;

    @Autowired
    private MockMvc mockMvc;

    @Captor
    private ArgumentCaptor<String> ipCaptor;

    @Test
    public void resendConfirmationEmail_commonScenario_200() throws Exception {
        mockMvc.perform(
            get("/resendRegistrationEmail")
                .queryParam("email", "email@mail.com")
                .with(remoteHost("10.10.10.10")))
            .andExpect(status().isOk());

        assertIpCheckedForTooManyResendAttempts("10.10.10.10");
        assertConfirmationEmailSent("email@mail.com");
        assertResendEmailAttemptRegisteredForIp("10.10.10.10");
    }

    @Test
    public void resendConfirmationEmail_resendingForbidden_400() throws Exception {
        expectSendConfirmationEmailForbidden();

        mockMvc.perform(
            get("/resendRegistrationEmail")
                .queryParam("email", "email@mail.com")
                .with(remoteHost("10.10.10.10")))
            .andExpect(status().isBadRequest());

        assertIpCheckedForTooManyResendAttempts("10.10.10.10");
        assertConfirmationEmailNotSent("email@mail.com");
        assertResendEmailAttemptNotRegisteredForIp("10.10.10.10");
    }

    @Test
    public void resendConfirmationEmail_tooManyAttemptsWithDirectIp_429() throws Exception {
        expectTooManyResendConfirmationEmailAttemptsForIp("10.10.10.10");

        mockMvc.perform(
            get("/resendRegistrationEmail")
                .queryParam("email", "email@mail.com")
                .with(remoteHost("10.10.10.10")))
            .andExpect(status().isTooManyRequests());

        assertIpCheckedForTooManyResendAttempts("10.10.10.10");
    }

    @Test
    public void resendConfirmationEmail_tooManyAttemptsWithXForwardedForHeaderWithFewIps_429() throws Exception {
        expectTooManyResendConfirmationEmailAttemptsForIp("10.10.10.10");

        mockMvc.perform(
            get("/resendRegistrationEmail")
                .queryParam("email", "email@mail.com")
                .header("X-Forwarded-For", "10.10.10.10,100.200.1.1"))
            .andExpect(status().isTooManyRequests());

        assertIpCheckedForTooManyResendAttempts("10.10.10.10");
    }

    @Test
    public void resendConfirmationEmail_tooManyAttemptsWithCorruptedXForwardedForHeader_429AndIpParameterIsNull() throws Exception {
        expectTooManyResendConfirmationEmailAttemptsForIp(null);

        mockMvc.perform(
            get("/resendRegistrationEmail")
                .queryParam("email", "email@mail.com")
                .header("X-Forwarded-For", "corrupted"))
            .andExpect(status().isTooManyRequests());

        assertIpCheckedForTooManyResendAttempts(null);
    }

    private void expectTooManyResendConfirmationEmailAttemptsForIp(String ip) {
        when(requestAttemptsService.areTooManyConfirmationEmailsResent(ip)).thenReturn(true);
    }

    private void expectSendConfirmationEmailForbidden() {
        when(registrationService.isSendConfirmationEmailForbidden(anyString())).thenReturn(true);
    }

    private void assertConfirmationEmailSent(String email) {
        verify(registrationService).sendConfirmationEmail(email);
    }

    private void assertConfirmationEmailNotSent(String email) {
        verify(registrationService, never()).sendConfirmationEmail(email);
    }

    private void assertResendEmailAttemptRegisteredForIp(String ip) {
        verify(requestAttemptsService).registerConfirmationEmailResent(ip);
    }

    private void assertResendEmailAttemptNotRegisteredForIp(String ip) {
        verify(requestAttemptsService, never()).registerConfirmationEmailResent(ip);
    }

    private void assertIpCheckedForTooManyResendAttempts(String ip) {
        verify(requestAttemptsService).areTooManyConfirmationEmailsResent(ipCaptor.capture());
        String actualIp = ipCaptor.getValue();
        assertThat(actualIp).isEqualTo(ip);
    }

    private static RequestPostProcessor remoteHost(String ip) {
        return request -> {
            request.setRemoteAddr(ip);
            return request;
        };
    }
}
