package dev.shelenkov.portfolio.web.controller.error;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

/**
 * Extension of BasicErrorController that returns 404 on direct attempt to access
 * {@code server.error.path} endpoint.
 */
@Controller
public class DefaultErrorController extends BasicErrorController {

    public DefaultErrorController(ErrorAttributes errorAttributes,
                                  ServerProperties serverProperties,
                                  ObjectProvider<? extends ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes,
            serverProperties.getError(),
            errorViewResolvers.orderedStream().collect(Collectors.toList()));
    }

    @Override
    protected HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode == null) {
            return HttpStatus.NOT_FOUND;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        } catch (RuntimeException ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
