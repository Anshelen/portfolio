package dev.shelenkov.portfolio.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shelenkov.portfolio.service.attempts.ResendConfirmationEmailAttemptsAware;
import dev.shelenkov.portfolio.service.registration.ConfirmEmailService;
import dev.shelenkov.portfolio.service.registration.RegistrationService;
import dev.shelenkov.portfolio.support.ConfiguredWebMvcTest;
import dev.shelenkov.portfolio.web.request.ResendConfirmationEmailRequest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static dev.shelenkov.portfolio.support.WebMvcUtils.remoteHost;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ConfiguredWebMvcTest(RegistrationController.class)
@MockBeans(
    @MockBean(classes = RegistrationService.class)
)
public class RegistrationControllerTests {

    @MockBean
    private ConfirmEmailService confirmEmailService;

    @MockBean
    private ResendConfirmationEmailAttemptsAware resendConfirmationEmailAttemptsService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Captor
    private ArgumentCaptor<String> ipCaptor;

    @Test
    public void resendConfirmationEmail_noCsrfToken_403() throws Exception {
        mockMvc.perform(
            post("/resendRegistrationEmail")
                .content(createRequestBody("email@mail.com"))
                .contentType(MediaType.APPLICATION_JSON)
                .with(remoteHost("10.10.10.10")))
            .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"email", "email@mail", "mail.ru"})
    public void resendConfirmationEmail_notValidEmails_400(String email) throws Exception {
        mockMvc.perform(
            post("/resendRegistrationEmail")
                .content(createRequestBody(email))
                .contentType(MediaType.APPLICATION_JSON)
                .with(remoteHost("10.10.10.10"))
                .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void resendConfirmationEmail_commonScenario_200() throws Exception {
        mockMvc.perform(
            post("/resendRegistrationEmail")
                .content(createRequestBody("email@mail.com"))
                .contentType(MediaType.APPLICATION_JSON)
                .with(remoteHost("10.10.10.10"))
                .with(csrf()))
            .andExpect(status().isOk());

        assertIpCheckedForTooManyResendAttempts("10.10.10.10");
        assertConfirmationEmailSent("email@mail.com");
        assertResendEmailAttemptRegisteredForIp("10.10.10.10");
    }

    @Test
    public void resendConfirmationEmail_resendingForbidden_400() throws Exception {
        expectSendConfirmationEmailForbidden();

        mockMvc.perform(
            post("/resendRegistrationEmail")
                .content(createRequestBody("email@mail.com"))
                .contentType(MediaType.APPLICATION_JSON)
                .with(remoteHost("10.10.10.10"))
                .with(csrf()))
            .andExpect(status().isBadRequest());

        assertIpCheckedForTooManyResendAttempts("10.10.10.10");
        assertConfirmationEmailNotSent("email@mail.com");
        assertResendEmailAttemptNotRegisteredForIp("10.10.10.10");
    }

    @Test
    public void resendConfirmationEmail_tooManyAttemptsWithDirectIp_429() throws Exception {
        expectTooManyResendConfirmationEmailAttemptsForIp("10.10.10.10");

        mockMvc.perform(
            post("/resendRegistrationEmail")
                .content(createRequestBody("email@mail.com"))
                .contentType(MediaType.APPLICATION_JSON)
                .with(remoteHost("10.10.10.10"))
                .with(csrf()))
            .andExpect(status().isTooManyRequests());

        assertIpCheckedForTooManyResendAttempts("10.10.10.10");
    }

    @Test
    public void resendConfirmationEmail_tooManyAttemptsWithXForwardedForHeaderWithFewIps_429() throws Exception {
        expectTooManyResendConfirmationEmailAttemptsForIp("10.10.10.10");

        mockMvc.perform(
            post("/resendRegistrationEmail")
                .content(createRequestBody("email@mail.com"))
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Forwarded-For", "10.10.10.10,100.200.1.1")
                .with(csrf()))
            .andExpect(status().isTooManyRequests());

        assertIpCheckedForTooManyResendAttempts("10.10.10.10");
    }

    @Test
    public void resendConfirmationEmail_corruptedXForwardedForHeader_400() throws Exception {
        mockMvc.perform(
            post("/resendRegistrationEmail")
                .content(createRequestBody("email@mail.com"))
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Forwarded-For", "corrupted")
                .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    private void expectTooManyResendConfirmationEmailAttemptsForIp(String ip) {
        when(resendConfirmationEmailAttemptsService.areTooManyConfirmationEmailsResent(ip)).thenReturn(true);
    }

    private void expectSendConfirmationEmailForbidden() {
        when(confirmEmailService.isSendConfirmationEmailForbidden(anyString())).thenReturn(true);
    }

    @SneakyThrows(IOException.class)
    private void assertConfirmationEmailSent(String email) {
        verify(confirmEmailService).sendConfirmationEmail(email);
    }

    @SneakyThrows(IOException.class)
    private void assertConfirmationEmailNotSent(String email) {
        verify(confirmEmailService, never()).sendConfirmationEmail(email);
    }

    private void assertResendEmailAttemptRegisteredForIp(String ip) {
        verify(resendConfirmationEmailAttemptsService).registerConfirmationEmailResent(ip);
    }

    private void assertResendEmailAttemptNotRegisteredForIp(String ip) {
        verify(resendConfirmationEmailAttemptsService, never()).registerConfirmationEmailResent(ip);
    }

    private void assertIpCheckedForTooManyResendAttempts(String ip) {
        verify(resendConfirmationEmailAttemptsService).areTooManyConfirmationEmailsResent(ipCaptor.capture());
        String actualIp = ipCaptor.getValue();
        assertThat(actualIp).isEqualTo(ip);
    }

    @SneakyThrows
    private String createRequestBody(String email) {
        ResendConfirmationEmailRequest data = new ResendConfirmationEmailRequest(email);
        return objectMapper.writeValueAsString(data);
    }
}
