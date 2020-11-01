package dev.shelenkov.portfolio.web.auxiliary;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which indicates that a method parameter should be bound to client IP address
 * string variable. Only IPv4 are supported. In case of not valid request data {@code null} can
 * be returned.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Ip {
}
