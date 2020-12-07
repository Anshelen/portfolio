package dev.shelenkov.portfolio.web.support.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Email;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation, expanding {@link Email} annotation. It guarantees that email
 * address contains a dot after @. E.g. "address@mail" is not more a valid email
 * address.
 */
@SuppressWarnings("unused")
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Email(regexp = ".+@.+\\..+")
@Constraint(validatedBy = {})
@Documented
public @interface ValidEmail {

    String message() default "Email address is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
