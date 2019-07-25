package dev.shelenkov.portfolio.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shelenkov.portfolio.annotations.ConfiguredWebMvcTest;
import dev.shelenkov.portfolio.service.mail.EmailService;
import dev.shelenkov.portfolio.web.wrappers.dto.EmailDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.MailSendException;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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

    @Test
    public void test_normal() throws Exception {
        EmailDTO data = new EmailDTO("name", "subject", "text");
        String body = objectMapper.writeValueAsString(data);
        mockMvc.perform(post("/email/send")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(body))
            .andExpect(status().isOk());
    }

    @Test
    public void test_validation_errors() throws Exception {
        EmailDTO data = new EmailDTO("a", null, "text");
        String body = objectMapper.writeValueAsString(data);
        mockMvc.perform(post("/email/send")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(body))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.violations", hasSize(2)))
            .andExpect(jsonPath("$.violations[*].fieldName", containsInAnyOrder("name", "subject")));
    }

    @Test
    public void test_server_error() throws Exception {
        doThrow(MailSendException.class).when(emailService)
            .sendSimpleEmail(anyString(), anyString(), anyString());

        EmailDTO data = new EmailDTO("name", "subject", "text");
        String body = objectMapper.writeValueAsString(data);

        mockMvc.perform(post("/email/send")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(body))
            .andExpect(status().is5xxServerError())
            .andExpect(jsonPath("$.message").value("Can't send e'mail"));

        verify(emailService, times(1))
            .sendSimpleEmail(anyString(), anyString(), anyString());
        verifyNoMoreInteractions(emailService);
    }
}