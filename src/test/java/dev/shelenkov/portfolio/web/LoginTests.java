package dev.shelenkov.portfolio.web;

import dev.shelenkov.portfolio.event.LoginEvent;
import dev.shelenkov.portfolio.service.attempts.LoginAttemptsAware;
import dev.shelenkov.portfolio.support.AutoConfigureMockApplicationEventPublisher;
import dev.shelenkov.portfolio.support.ConfiguredWebMvcTest;
import dev.shelenkov.portfolio.support.MockApplicationEventPublisher;
import dev.shelenkov.portfolio.support.ip.CorruptedIpException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URLEncoder;

import static dev.shelenkov.portfolio.support.SecurityConstants.ADMIN_EMAIL;
import static dev.shelenkov.portfolio.support.SecurityConstants.ADMIN_ID;
import static dev.shelenkov.portfolio.support.SecurityConstants.ADMIN_PASSWORD;
import static dev.shelenkov.portfolio.support.SecurityConstants.DISABLED_USER_EMAIL;
import static dev.shelenkov.portfolio.support.SecurityConstants.DISABLED_USER_PASSWORD;
import static dev.shelenkov.portfolio.support.SecurityConstants.ENABLED_USER_EMAIL;
import static dev.shelenkov.portfolio.support.SecurityConstants.ENABLED_USER_ID;
import static dev.shelenkov.portfolio.support.SecurityConstants.ENABLED_USER_NAME;
import static dev.shelenkov.portfolio.support.SecurityConstants.ENABLED_USER_PASSWORD;
import static dev.shelenkov.portfolio.support.WebMvcUtils.remoteHost;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ConfiguredWebMvcTest
@AutoConfigureMockApplicationEventPublisher
public class LoginTests {

    @MockBean
    private LoginAttemptsAware loginAttemptsAwareService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MockApplicationEventPublisher publisher;

    @Test
    public void login_successLogin_redirectToDefaultSuccessUrlAndLoginEventFired() throws Exception {
        mockMvc.perform(
            post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("email", ENABLED_USER_EMAIL)
                .param("password", ENABLED_USER_PASSWORD)
                .with(csrf())
                .with(remoteHost("77.88.33.22")))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/"));
        publisher.assertEventsWereFired(new LoginEvent(ENABLED_USER_ID, "77.88.33.22"));
    }

    @Test
    public void login_navigateToRestrictedUrl_loginAndRedirectToUrl() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(get("/admin").session(session))
            .andExpect(status().isFound())
            .andExpect(redirectedUrlPattern("http://*/login"))
            .andReturn();

        mockMvc.perform(
            post("/login")
                .session(session)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("email", ADMIN_EMAIL)
                .param("password", ADMIN_PASSWORD)
                .with(csrf()))
            .andExpect(status().isFound())
            .andExpect(redirectedUrlPattern("http://*/admin"));

        publisher.assertEventsWereFired(new LoginEvent(ADMIN_ID, "127.0.0.1"));
    }

    @Test
    public void login_noCsrfToken_403() throws Exception {
        mockMvc.perform(
            post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("email", ENABLED_USER_EMAIL)
                .param("password", ENABLED_USER_PASSWORD))
            .andExpect(status().isForbidden());
        publisher.assertNoEventsWereFired();
    }

    @Test
    public void login_corruptedIpInHeader_exception() {
        assertThatExceptionOfType(CorruptedIpException.class).isThrownBy(() ->
            mockMvc.perform(
                post("/login")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .param("email", ENABLED_USER_NAME)
                    .param("password", ENABLED_USER_PASSWORD)
                    .header("X-Forwarded-For", "corrupted")
                    .with(csrf())));
        publisher.assertNoEventsWereFired();
    }

    @Test
    public void login_invalidCredentials_redirectToLoginPageWithBadCredentialsParameter() throws Exception {
        mockMvc.perform(
            post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("email", ENABLED_USER_EMAIL)
                .param("password", "invalid")
                .with(csrf()))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/login?error=BadCredentials"));
        publisher.assertNoEventsWereFired();
    }

    @Test
    public void login_disabledAccount_redirectToLoginPageWithDisabledParameter() throws Exception {
        mockMvc.perform(
            post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("email", DISABLED_USER_EMAIL)
                .param("password", DISABLED_USER_PASSWORD)
                .with(csrf()))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl(
                "/login?error=Disabled&email=" + URLEncoder.encode(DISABLED_USER_EMAIL, "UTF-8")));
        publisher.assertNoEventsWereFired();
    }

    @Test
    public void login_tooManyFailedLoginAttempts_redirectToLoginPageWithTooManyAttemptsParameter() throws Exception {
        expectTooManyFailedLoginAttempts();

        mockMvc.perform(
            post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("email", ENABLED_USER_EMAIL)
                .param("password", ENABLED_USER_PASSWORD)
                .with(csrf()))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/login?error=TooManyAttempts"));
        publisher.assertNoEventsWereFired();
    }

    private void expectTooManyFailedLoginAttempts() {
        when(loginAttemptsAwareService.areTooManyFailedLoginAttempts(any())).thenReturn(true);
    }
}
