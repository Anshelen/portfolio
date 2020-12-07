package dev.shelenkov.portfolio.security.internal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class LoggingAccessDeniedHandler extends AccessDeniedHandlerImpl {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        String requestUri = request.getRequestURI();
        Map<String, List<String>> parameterMap = getParametersMap(request);
        String principalName = getPrincipalName(request);
        log.warn("Access denied. URI: {}, params: {}, principal: {}, message: {}",
            requestUri, parameterMap, principalName, accessDeniedException.getMessage());

        super.handle(request, response, accessDeniedException);
    }

    private Map<String, List<String>> getParametersMap(HttpServletRequest request) {
        return request.getParameterMap().entrySet().stream()
            .filter(e -> !"password".equals(e.getKey()))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> Arrays.asList(e.getValue())));
    }

    private String getPrincipalName(HttpServletRequest request) {
        if (request.getUserPrincipal() == null) {
            return "ANONYMOUS";
        } else {
            return request.getUserPrincipal().getName();
        }
    }
}
