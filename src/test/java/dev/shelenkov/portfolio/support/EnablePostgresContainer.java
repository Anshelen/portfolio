package dev.shelenkov.portfolio.support;

import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Launches PostgreSQL docker container if it wasn't started before.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ContextConfiguration(initializers = PostgreSQLContainerStarter.Initializer.class)
@IntegrationTest
public @interface EnablePostgresContainer {
}
