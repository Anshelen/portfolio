package dev.shelenkov.portfolio.web.convert;

import dev.shelenkov.portfolio.web.response.ValidationErrorResponse;
import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Mapper
public interface BindingResultToValidationErrorResponseConverter
    extends Converter<BindingResult, ValidationErrorResponse> {

    @Override
    default ValidationErrorResponse convert(BindingResult bindingResult) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            error.addViolation(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return error;
    }
}
