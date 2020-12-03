package dev.shelenkov.portfolio.support;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;

public final class PostgreSQLContainerStarter {

    private static final String POSTGRES_VERSION = "postgres:11.9";

    @SuppressWarnings("resource")
    private final static PostgreSQLContainer<?> CONTAINER = new PostgreSQLContainer<>(POSTGRES_VERSION)
        .withUsername("user")
        .withPassword("password")
        .withDatabaseName("test");

    static {
        CONTAINER.start();
    }

    private PostgreSQLContainerStarter() {
    }

    @SuppressWarnings("PublicInnerClass")
    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                applicationContext,
                "spring.test.database.replace=NONE",
                "spring.datasource.url=" + CONTAINER.getJdbcUrl(),
                "spring.datasource.username=" + CONTAINER.getUsername(),
                "spring.datasource.password=" + CONTAINER.getPassword()
            );
        }
    }
}
