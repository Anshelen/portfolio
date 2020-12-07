package dev.shelenkov.portfolio.web.security.oauth2;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

/**
 * Exception thrown when unable to fetch user email from OAuth2 provider.
 */
public class OAuth2NoVerifiedEmailException extends OAuth2AuthenticationException {
    public OAuth2NoVerifiedEmailException(OAuth2Error error, String message) {
        super(error, message);
    }
}
