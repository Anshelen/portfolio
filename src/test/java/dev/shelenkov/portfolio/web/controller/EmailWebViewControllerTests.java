package dev.shelenkov.portfolio.web.controller;

import dev.shelenkov.portfolio.support.ConfiguredWebMvcTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ActiveProfiles("test")
@ConfiguredWebMvcTest(EmailWebViewController.class)
class EmailWebViewControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void viewMail_Ok_Success() throws Exception {
        String token = UUID.randomUUID().toString();
        String anyParam = "anyParamValue";
        mockMvc.perform(
            get("/mail/verifyEmailAddress")
                .param("token", token)
                .param("anyParam", anyParam))
            .andExpect(status().isOk())
            .andExpect(content().contentType(
                new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8)))
            .andExpect(model().attribute("webVersion", true))
            .andExpect(model().attribute("rootUrl", "http://test.ru/app"))
            .andExpect(model().attribute("token", token))
            .andExpect(model().attribute("anyParam", anyParam))
            .andExpect(view().name("email/verifyEmailAddress"));
    }

    @Test
    public void viewMail_UnknownViewName_404() throws Exception {
        mockMvc.perform(get("/mail/unknownTemplate"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void viewMail_IllegalViewNameWithDot_404() throws Exception {
        mockMvc.perform(get("/mail/badTemplate.Name"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void viewMail_IllegalViewNameWithSlash_404() throws Exception {
        mockMvc.perform(get("/mail/badTemplate/Name"))
            .andExpect(status().isNotFound());
    }
}
