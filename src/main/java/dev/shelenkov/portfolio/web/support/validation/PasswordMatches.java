package dev.shelenkov.portfolio.web.support.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Verifies that "password" and "repeat password" fields of user data DTO while
 * registering are equal.
 */
@SuppressWarnings("unused")
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Documented
public @interface PasswordMatches {

    String message() default "{javax.validation.constraints.PasswordMatches.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
