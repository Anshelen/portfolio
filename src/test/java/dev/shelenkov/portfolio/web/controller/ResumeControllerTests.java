package dev.shelenkov.portfolio.web.controller;

import dev.shelenkov.portfolio.model.resume.ResumeFormat;
import dev.shelenkov.portfolio.model.resume.ResumeLanguage;
import dev.shelenkov.portfolio.service.resume.ResumeResourceFactory;
import dev.shelenkov.portfolio.support.ConfiguredWebMvcTest;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ConfiguredWebMvcTest(ResumeController.class)
class ResumeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResumeResourceFactory resumeResourceFactory;

    @Value("classpath:resume/test_resume.pdf")
    private Resource resumeResource;

    @Test
    public void downloadResume_Ok_Success() throws Exception {
        doReturn(resumeResource).when(resumeResourceFactory)
            .getResumeResource(ResumeLanguage.ENGLISH, ResumeFormat.PDF);
        MvcResult result = mockMvc.perform(get("/resume?lang=En&format=PDF"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_PDF_VALUE))
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + resumeResource.getFilename() + '"'))
            .andReturn();
        byte[] expected = IOUtils.toByteArray(resumeResource.getInputStream());
        assertArrayEquals(expected, result.getResponse().getContentAsByteArray());
        verify(resumeResourceFactory, times(1))
            .getResumeResource(ResumeLanguage.ENGLISH, ResumeFormat.PDF);
        verifyNoMoreInteractions(resumeResourceFactory);
    }

    @Test
    public void downloadResume_MissedParameter_400() throws Exception {
        mockMvc.perform(get("/resume?format=PDF"))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void downloadResume_NotSupportedParameterValue_400() throws Exception {
        mockMvc.perform(get("/resume?lang=UA&format=PDF"))
            .andExpect(status().isBadRequest());
    }
}
