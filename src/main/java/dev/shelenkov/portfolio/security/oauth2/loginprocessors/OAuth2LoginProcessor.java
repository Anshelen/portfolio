package dev.shelenkov.portfolio.security.oauth2.loginprocessors;

import dev.shelenkov.portfolio.security.ExtendedUser;

import java.util.Map;

/**
 * Contains logic of processing OAuth2 user during the login or registration
 * process.
 */
public interface OAuth2LoginProcessor {
    /**
     * Create user principal and register user if needed.
     *
     * @param userAttributes user attributes from OAuth2 provider
     * @param accessToken    OAuth2 provider access token
     * @return user principal
     */
    ExtendedUser processUser(Map<String, Object> userAttributes, String accessToken);

    /**
     * Returns if the processor is applicable for provider with such id.
     *
     * @param registrationId provider id
     * @return is processor can work with such provider
     */
    boolean isRegistrationIdSupported(String registrationId);
}
