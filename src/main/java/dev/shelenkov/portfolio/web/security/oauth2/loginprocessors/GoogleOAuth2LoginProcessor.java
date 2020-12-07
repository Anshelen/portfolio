package dev.shelenkov.portfolio.web.security.oauth2.loginprocessors;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.repository.AccountRepository;
import dev.shelenkov.portfolio.service.registration.IRegistrationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GoogleOAuth2LoginProcessor extends AbstractOAuth2LoginProcessor {

    private final IRegistrationService registrationService;

    public GoogleOAuth2LoginProcessor(AccountRepository accountRepository,
                                      ClientRegistrationRepository clientRegistrationRepository,
                                      IRegistrationService registrationService) {
        super(accountRepository, clientRegistrationRepository);
        this.registrationService = registrationService;
    }

    @Override
    public String getSupportedRegistrationId() {
        return CommonOAuth2Provider.GOOGLE.name();
    }

    @Override
    protected Account loadUserByOauth2Id(String oauth2Id) {
        return getAccountRepository().getByGoogleId(oauth2Id);
    }

    @Override
    public Account registerNewAccount(Map<String, Object> userAttributes,
                                      String email,
                                      String oauth2Id) {
        String username = (String) userAttributes.get("name");
        return registrationService.registerNewGoogleUser(username, email, oauth2Id);
    }

    @Override
    protected void setOAuth2IdForAccount(Account account, String oauth2Id) {
        account.setGoogleId(oauth2Id);
    }

    @Override
    protected String getVerifiedEmail(Map<String, Object> userAttributes, String accessToken) {
        String email = (String) userAttributes.get("email");
        Boolean emailVerified = (Boolean) userAttributes.get("email_verified");
        if (StringUtils.isEmpty(email) || (emailVerified == null)) {
            throw new RuntimeException(
                "Can't fetch email from OAuth2 provider. Attributes: " + userAttributes);
        }

        return emailVerified ? email : null;
    }
}
