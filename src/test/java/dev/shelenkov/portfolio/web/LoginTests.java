package dev.shelenkov.portfolio.web;

import dev.shelenkov.portfolio.annotations.ConfiguredWebMvcTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URLEncoder;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO: 04.11.2020 set default value to @ConfiguredWebMvcTest
@ConfiguredWebMvcTest(Void.class)
public class LoginTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void login_successLogin_redirectToDefaultSuccessUrl() throws Exception {
        mockMvc.perform(
            post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("email", "user@mail.ru")
                .param("password", "user")
                .with(csrf()))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/"));
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
                .param("email", "admin@mail.ru")
                .param("password", "admin")
                .with(csrf()))
            .andExpect(status().isFound())
            .andExpect(redirectedUrlPattern("http://*/admin"));
    }

    @Test
    public void login_noCsrfToken_403() throws Exception {
        mockMvc.perform(
            post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("email", "user@mail.ru")
                .param("password", "user"))
            .andExpect(status().isForbidden());
    }

    @Test
    public void login_invalidCredentials_redirectToLoginPageWithBadCredentialsParameter() throws Exception {
        mockMvc.perform(
            post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("email", "user@mail.ru")
                .param("password", "invalid")
                .with(csrf()))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/login?error=BadCredentials"));
    }

    @Test
    public void login_disabledAccount_redirectToLoginPageWithDisabledParameter() throws Exception {
        mockMvc.perform(
            post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("email", "disabled@mail.ru")
                .param("password", "disabled")
                .with(csrf()))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl(
                "/login?error=Disabled&email=" + URLEncoder.encode("disabled@mail.ru", "UTF-8")));
    }
}
