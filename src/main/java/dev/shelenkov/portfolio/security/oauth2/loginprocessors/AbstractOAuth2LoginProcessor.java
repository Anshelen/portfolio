package dev.shelenkov.portfolio.security.oauth2.loginprocessors;

import dev.shelenkov.portfolio.model.Account;
import dev.shelenkov.portfolio.repository.AccountRepository;
import dev.shelenkov.portfolio.security.ExtendedUser;
import dev.shelenkov.portfolio.security.SecurityUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import javax.validation.constraints.NotNull;
import java.util.Locale;
import java.util.Map;

import static dev.shelenkov.portfolio.security.oauth2.OAuth2ErrorHelper.throwOnNoUserNameAttributeSpecified;
import static dev.shelenkov.portfolio.security.oauth2.OAuth2ErrorHelper.throwOnNotKnownRegistrationId;
import static dev.shelenkov.portfolio.security.oauth2.OAuth2ErrorHelper.throwOnNotVerifiedEmail;

@RequiredArgsConstructor
public abstract class AbstractOAuth2LoginProcessor implements OAuth2LoginProcessor {

    @Getter
    private final AccountRepository accountRepository;
    private final ClientRegistrationRepository clientRegistrationRepository;

    @SuppressWarnings("FeatureEnvy")
    @Override
    public ExtendedUser processUser(Map<String, Object> userAttributes, String accessToken) {
        String oauth2Id = getOauth2Id(userAttributes);
        Account account = loadUserByOauth2Id(oauth2Id);

        if (account == null) {
            String email = getVerifiedEmail(userAttributes, accessToken);
            if (email == null) {
                return throwOnNotVerifiedEmail(oauth2Id);
            }

            // If user with such email already exists, then attach oauth id to
            // him or else create new user
            account = accountRepository.getByEmail(email);
            if (account == null) {
                account = registerNewAccount(userAttributes, email, oauth2Id);
            } else {
                setOAuth2IdForAccount(account, oauth2Id);
                accountRepository.save(account);
            }
        }

        return new ExtendedUser(
            account.getEmail(),
            account.getPassword(),
            account.isEnabled(),
            SecurityUtils.generateAuthoritiesList(account),
            account.getUsername(),
            userAttributes);
    }

    @Override
    public boolean isRegistrationIdSupported(String registrationId) {
        return getSupportedRegistrationId().equalsIgnoreCase(registrationId);
    }

    /**
     * Returns user account by its OAuth2 id or {@code null} if not exist.
     */
    protected abstract Account loadUserByOauth2Id(String oauth2Id);

    /**
     * Returns name of supported OAuth2 provider.
     */
    protected abstract @NotNull String getSupportedRegistrationId();

    /**
     * Creates new user account.
     *
     * @param userAttributes user profile attributes from OAuth2 provider
     * @param email user email
     * @param oauth2Id user id from OAuth2 provider
     * @return created account
     */
    protected abstract Account registerNewAccount(Map<String, Object> userAttributes,
                                                  String email,
                                                  String oauth2Id);

    /**
     * Sets OAuth2 id to account.
     */
    protected abstract void setOAuth2IdForAccount(Account account, String oauth2Id);

    /**
     * Fetches verified email from OAuth2 provider.
     *
     * @param userAttributes user profile attributes from OAuth2 provider
     * @param accessToken OAuth2 access token
     * @return user email
     */
    protected abstract @Nullable String getVerifiedEmail(Map<String, Object> userAttributes,
                                                         String accessToken);

    private String getOauth2Id(Map<String, Object> userAttributes) {
        String registrationId = getSupportedRegistrationId().toLowerCase(Locale.ENGLISH);

        ClientRegistration registration = clientRegistrationRepository.findByRegistrationId(registrationId);
        if (registration == null) {
            return throwOnNotKnownRegistrationId(registrationId);
        }

        String userNameAttributeName = registration.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        if (userNameAttributeName == null) {
            return throwOnNoUserNameAttributeSpecified(registration);
        }

        return userAttributes.get(userNameAttributeName).toString();
    }
}
