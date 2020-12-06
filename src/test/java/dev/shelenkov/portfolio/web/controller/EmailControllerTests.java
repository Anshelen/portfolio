package dev.shelenkov.portfolio.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shelenkov.portfolio.service.auxiliary.ISendEmailToAdminAttemptsAware;
import dev.shelenkov.portfolio.service.mail.EmailService;
import dev.shelenkov.portfolio.support.ConfiguredWebMvcTest;
import dev.shelenkov.portfolio.web.wrappers.dto.EmailDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ConfiguredWebMvcTest(EmailController.class)
public class EmailControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailService emailService;

    @MockBean
    private ISendEmailToAdminAttemptsAware sendEmailToAdminAttemptsAwareService;

    @Test
    public void test_noCsrfToken_forbidden() throws Exception {
        EmailDTO data = new EmailDTO("name", "subject", "text");
        String body = objectMapper.writeValueAsString(data);
        mockMvc.perform(post("/email/send")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(body))
            .andExpect(status().isForbidden());
    }

    @Test
    public void test_normal() throws Exception {
        EmailDTO data = new EmailDTO("name", "subject", "text");
        String body = objectMapper.writeValueAsString(data);
        mockMvc.perform(post("/email/send")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(body)
            .with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    public void test_validation_errors() throws Exception {
        EmailDTO data = new EmailDTO("a", null, "text");
        String body = objectMapper.writeValueAsString(data);
        mockMvc.perform(post("/email/send")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(body)
            .with(csrf()))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.violations", hasSize(2)))
            .andExpect(jsonPath("$.violations[*].fieldName", containsInAnyOrder("name", "subject")));
    }

    @Test
    public void test_server_error() throws Exception {
        doThrow(IOException.class).when(emailService)
            .sendSimpleEmailToAdmin(anyString(), anyString(), anyString());

        EmailDTO data = new EmailDTO("name", "subject", "text");
        String body = objectMapper.writeValueAsString(data);

        mockMvc.perform(post("/email/send")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(body)
            .with(csrf()))
            .andExpect(status().is5xxServerError())
            .andExpect(jsonPath("$.message").value("Can't send e'mail"));

        verify(emailService, times(1))
            .sendSimpleEmailToAdmin(anyString(), anyString(), anyString());
        verifyNoMoreInteractions(emailService);
    }

    @Test
    public void test_tooManySendAttempts_429() throws Exception {
        when(sendEmailToAdminAttemptsAwareService.areTooManyEmailsToAdminSent(any())).thenReturn(true);

        EmailDTO data = new EmailDTO("name", "subject", "text");
        String body = objectMapper.writeValueAsString(data);
        mockMvc.perform(post("/email/send")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(body)
            .with(csrf()))
            .andExpect(status().isTooManyRequests());
    }
}
