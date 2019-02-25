package dev.shelenkov.portfolio;

import dev.shelenkov.portfolio.annotations.ConfiguredWebMvcTest;
import dev.shelenkov.portfolio.web.controller.HomeController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ConfiguredWebMvcTest(HomeController.class)
public class ApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHome() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
    }
}
