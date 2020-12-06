package dev.shelenkov.portfolio.support;

import org.springframework.security.test.context.support.WithUserDetails;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signals that test (or all tests in class) should be performed on the behalf
 * of the authorized user.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithUserDetails(SecurityConstants.ENABLED_USER_EMAIL)
public @interface WithUser {
}
