package dev.shelenkov.portfolio.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Handler to redirect users after not successful authentication. Based on
 * {@link ExceptionMappingAuthenticationFailureHandler}. Additionally replaces
 * fragments like {@code ${param}} with corresponding request parameter.
 * Supports only individual parameters.
 */
public class ParametersMappingAuthenticationFailureHandler
    extends SimpleUrlAuthenticationFailureHandler {

    private final Map<String, String> failureUrlMap = new HashMap<>();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
        throws IOException, ServletException {

        String url = failureUrlMap.get(exception.getClass().getName());
        if (url != null) {
            for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
                String param = entry.getKey();
                String[] values = entry.getValue();
                if (values.length > 1) {
                    continue;
                }
                url = url.replaceAll(
                    Pattern.quote("${" + param + '}'),
                    URLEncoder.encode(values[0], "UTF-8"));
            }
            getRedirectStrategy().sendRedirect(request, response, url);
        } else {
            super.onAuthenticationFailure(request, response, exception);
        }
    }

    /**
     * Sets the map of exception types (by name) to URLs.
     *
     * @param failureUrlMap the map keyed by the fully-qualified name of the exception
     *                      class, with the corresponding failure URL as the value.
     * @throws IllegalArgumentException if the entries are not Strings or the URL is not
     *                                  valid.
     */
    public void setExceptionMappings(Map<?, ?> failureUrlMap) {
        this.failureUrlMap.clear();
        for (Map.Entry<?, ?> entry : failureUrlMap.entrySet()) {
            Object exception = entry.getKey();
            Object url = entry.getValue();
            Assert.isInstanceOf(String.class, exception,
                "Exception key must be a String (the exception classname).");
            Assert.isInstanceOf(String.class, url, "URL must be a String");
            Assert.isTrue(UrlUtils.isValidRedirectUrl((String) url),
                () -> "Not a valid redirect URL: " + url);
            this.failureUrlMap.put((String) exception, (String) url);
        }
    }
}
