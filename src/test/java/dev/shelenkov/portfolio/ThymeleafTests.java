package dev.shelenkov.portfolio;

import org.junit.jupiter.api.Test;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;

import static org.assertj.core.api.Assertions.assertThat;

public class ThymeleafTests {

    @Test
    public void test() {
        TestExecutor executor = new TestExecutor();
        executor.execute("classpath:thymeleaftests");
        assertThat(executor.isAllOK()).isTrue();
    }
}
