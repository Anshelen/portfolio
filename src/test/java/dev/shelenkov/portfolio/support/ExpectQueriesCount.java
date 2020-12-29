package dev.shelenkov.portfolio.support;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for adding the check of number of queries to database performed during test
 * execution. That mechanism helps to control lazy loading of dependent entities by
 * Hibernate.
 *
 * <p>To work correctly {@link com.yannbriancon.interceptor.HibernateQueryInterceptor
 * HibernateQueryInterceptor} must be present in context and be properly registered.
 * It can be achieved by annotating test class with
 * {@link org.springframework.boot.test.context.SpringBootTest @SpringBootTest} or
 * {@link EnableQueryCounter @EnableQueryCounter}.</p>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ExpectQueriesCount {

    long value();
}
