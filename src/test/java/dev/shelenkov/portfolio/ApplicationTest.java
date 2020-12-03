package dev.shelenkov.portfolio;

import dev.shelenkov.portfolio.support.EnablePostgresContainer;
import dev.shelenkov.portfolio.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnablePostgresContainer
@IntegrationTest
public class ApplicationTest {

    @Test
    public void testContextLoaded() {
    }
}
