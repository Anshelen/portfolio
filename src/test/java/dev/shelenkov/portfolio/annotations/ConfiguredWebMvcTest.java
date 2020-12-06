package dev.shelenkov.portfolio.annotations;

import dev.shelenkov.portfolio.security.SpringSecurityTestConfig;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Extension of {@link WebMvcTest} annotation with additional configuration
 * test classes.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@WebMvcTest
@Import(SpringSecurityTestConfig.class)
public @interface ConfiguredWebMvcTest {

    /**
     * Specifies the controllers to test. May be left blank if all {@code @Controller}
     * beans should be added to the application context.
     *
     * @return the controllers to test
     * @see WebMvcTest#controllers()
     */
    @AliasFor(annotation = WebMvcTest.class, attribute = "controllers")
    Class<?>[] value() default Void.class;
}
