package dev.shelenkov.portfolio.support;

import org.springframework.context.annotation.ComponentScan;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for registering {@link com.yannbriancon.interceptor.HibernateQueryInterceptor
 * HibernateQueryInterceptor} in context. It is automatically registered via autoconfiguration
 * mechanism if it is enabled (e.g. when using
 * {@link org.springframework.boot.test.context.SpringBootTest @SpringBootTest}). So this annotation
 * is needed primarily in conjunction with
 * {@link org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest @DataJpaTest} annotation.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ComponentScan(basePackages = "com.yannbriancon")
public @interface EnableQueryCounter {
}
