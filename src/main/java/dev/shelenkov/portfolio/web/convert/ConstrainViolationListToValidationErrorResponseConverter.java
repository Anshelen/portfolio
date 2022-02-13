package dev.shelenkov.portfolio.web.convert;

import dev.shelenkov.portfolio.web.response.ValidationErrorResponse;
import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;

import javax.validation.ConstraintViolation;
import java.util.Collection;

@Mapper
public interface ConstrainViolationListToValidationErrorResponseConverter
    extends Converter<Collection<ConstraintViolation<?>>, ValidationErrorResponse> {

    @Override
    default ValidationErrorResponse convert(Collection<ConstraintViolation<?>> violations) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        for (ConstraintViolation<?> violation : violations) {
            error.addViolation(violation.getPropertyPath().toString(), violation.getMessage());
        }
        return error;
    }
}
