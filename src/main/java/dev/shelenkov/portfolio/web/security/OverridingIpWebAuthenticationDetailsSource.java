package dev.shelenkov.portfolio.web.security;

import dev.shelenkov.portfolio.web.support.ip.IpUtils;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Extension of {@link WebAuthenticationDetailsSource} for creating {@link WebAuthenticationDetails}
 * with modified remote address.
 */
public class OverridingIpWebAuthenticationDetailsSource extends WebAuthenticationDetailsSource {

    @Override
    public WebAuthenticationDetails buildDetails(HttpServletRequest context) {
        return new WebAuthenticationDetails(
            new OverridingIpWebAuthenticationDetailsSource.HttpServletRequestDecorator(context));
    }

    private static class HttpServletRequestDecorator extends HttpServletRequestWrapper {

        HttpServletRequestDecorator(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getRemoteAddr() {
            HttpServletRequest request = (HttpServletRequest) getRequest();
            return IpUtils.extractIp(request);
        }
    }
}
