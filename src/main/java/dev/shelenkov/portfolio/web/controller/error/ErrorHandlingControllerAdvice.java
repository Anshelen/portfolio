package dev.shelenkov.portfolio.web.controller.error;

import dev.shelenkov.portfolio.support.ip.CorruptedIpException;
import dev.shelenkov.portfolio.web.response.ValidationErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.OptimisticLockException;
import javax.validation.ConstraintViolationException;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ErrorHandlingControllerAdvice {

    private final ConversionService conversionService;

    @ExceptionHandler(CorruptedIpException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void onCorruptedIpException(CorruptedIpException e) {
        log.warn(e.getMessage());
    }

    @ExceptionHandler(OptimisticLockException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public void onOptimisticLockException(OptimisticLockException e) {
        log.warn(e.getMessage());
    }

    @ExceptionHandler({
        MethodArgumentTypeMismatchException.class,
        MissingServletRequestParameterException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void onMethodArgumentTypeMismatchException(Exception e) {
        log.warn(e.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void onHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn(e.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public void onResponseStatusException(ResponseStatusException e) {
        throw e;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponse onConstraintViolationException(ConstraintViolationException e) {
        return conversionService.convert(e.getConstraintViolations(), ValidationErrorResponse.class);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponse onMethodArgumentNotValidException(
        MethodArgumentNotValidException e) {
        return conversionService.convert(e.getBindingResult(), ValidationErrorResponse.class);
    }

    @ExceptionHandler(RuntimeException.class)
    public void onUnexpectedRuntimeException(RuntimeException e) {
        log.error("Unexpected exception", e);
        throw e;
    }
}
