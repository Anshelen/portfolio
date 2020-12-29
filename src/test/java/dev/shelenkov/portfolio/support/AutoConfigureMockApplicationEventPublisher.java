package dev.shelenkov.portfolio.support;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that can be applied to a spring test class to enable and configure auto-configuration
 * of a single {@link MockApplicationEventPublisher MockApplicationEventPublisher}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(MockApplicationEventPublisherAutoConfiguration.class)
public @interface AutoConfigureMockApplicationEventPublisher {
}
