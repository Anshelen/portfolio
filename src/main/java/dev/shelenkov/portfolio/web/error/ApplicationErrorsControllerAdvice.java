package dev.shelenkov.portfolio.web.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
@Slf4j
public class ApplicationErrorsControllerAdvice {

    @ExceptionHandler(CorruptedIpException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void onCorruptedIpException(CorruptedIpException e) {
        log.warn(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void onMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn(e.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public void onResponseStatusException(ResponseStatusException e) {
        throw e;
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void onUnexpectedRuntimeException(RuntimeException e) {
        log.error("Unexpected exception", e);
    }
}
