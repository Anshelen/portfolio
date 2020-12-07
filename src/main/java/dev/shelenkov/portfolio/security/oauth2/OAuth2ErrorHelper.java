package dev.shelenkov.portfolio.security.oauth2;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.web.client.RestClientException;

/**
 * Class for throwing OAuth2 related errors.
 */
@Slf4j
@UtilityClass
public class OAuth2ErrorHelper {

    private final String NOT_KNOWN_REGISTRATION_ID_ERROR_CODE = "not_known_registration_id";
    private final String NO_VERIFIED_EMAIL_ERROR_CODE = "no_verified_email";
    private final String MISSING_USER_INFO_URI_ERROR_CODE = "missing_user_info_uri";
    private final String MISSING_USER_NAME_ATTRIBUTE_ERROR_CODE = "missing_user_name_attribute";
    private final String INVALID_USER_INFO_RESPONSE_ERROR_CODE = "invalid_user_info_response";
    private final String UNSUPPORTED_PROVIDER_ERROR_CODE = "unsupported_provider";

    public static <T> T throwOnNotKnownRegistrationId(String registrationId) {
        OAuth2Error oauth2Error = new OAuth2Error(
            NOT_KNOWN_REGISTRATION_ID_ERROR_CODE,
            "Not known registration id: " + registrationId,
            null
        );
        log.error("OAuth2 error: {}", oauth2Error);
        throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
    }

    public static <T> T throwOnNotVerifiedEmail(String oauth2Id) {
        OAuth2Error oauth2Error = new OAuth2Error(
            NO_VERIFIED_EMAIL_ERROR_CODE,
            "Not verified email for user: " + oauth2Id,
            null
        );
        log.warn("OAuth2 error: {}", oauth2Error);
        throw new OAuth2NoVerifiedEmailException(oauth2Error, oauth2Error.toString());
    }

    public static <T> T throwOnNoUserNameAttributeSpecified(ClientRegistration registration) {
        OAuth2Error oauth2Error = new OAuth2Error(
            MISSING_USER_NAME_ATTRIBUTE_ERROR_CODE,
            "Missing required \"user name\" attribute name in UserInfoEndpoint for Client Registration: " +
                registration.getRegistrationId(),
            null
        );
        log.error("OAuth2 error: {}", oauth2Error);
        throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
    }

    public static <T> T throwOnNoUriSpecified(ClientRegistration registration) {
        OAuth2Error oauth2Error = new OAuth2Error(
            MISSING_USER_INFO_URI_ERROR_CODE,
            "Missing required UserInfo Uri in UserInfoEndpoint for Client Registration: " +
                registration.getRegistrationId(),
            null
        );
        log.error("OAuth2 error: {}", oauth2Error);
        throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
    }

    public static <T> T throwOnCaughtOAuth2Exception(ClientRegistration registration,
                                                     OAuth2AuthorizationException ex) {
        OAuth2Error oauth2Error = ex.getError();
        StringBuilder errorDetails = new StringBuilder(100);
        errorDetails
            .append("An error occurred while attempting to retrieve the UserInfo Resource: ")
            .append("Error details: [")
            .append("UserInfo Uri: ").append(registration.getProviderDetails().getUserInfoEndpoint().getUri())
            .append(", Error Code: ").append(oauth2Error.getErrorCode());
        if (oauth2Error.getDescription() != null) {
            errorDetails.append(", Error Description: ")
                .append(oauth2Error.getDescription());
        }
        errorDetails.append(']');

        OAuth2Error resultError = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
            "An error occurred while attempting to retrieve the UserInfo Resource: " + errorDetails, null);
        log.error("OAuth2 error: {}", resultError);
        throw new OAuth2AuthenticationException(resultError, resultError.toString(), ex);
    }

    public static <T> T throwOnRestClientException(RestClientException ex) {
        OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
            "An error occurred while attempting to retrieve the UserInfo Resource: " + ex.getMessage(), null);
        log.error("OAuth2 error: {}", oauth2Error);
        throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
    }

    public static <T> T throwOnNotSupportedProviderException(ClientRegistration registration) {
        OAuth2Error oauth2Error = new OAuth2Error(UNSUPPORTED_PROVIDER_ERROR_CODE,
            "Not supported registration: " + registration.getRegistrationId(), null);
        log.error("OAuth2 error: {}", oauth2Error);
        throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
    }
}
