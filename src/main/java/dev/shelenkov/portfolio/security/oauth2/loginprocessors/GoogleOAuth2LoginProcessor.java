package dev.shelenkov.portfolio.security.oauth2.loginprocessors;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.service.account.AccountService;
import dev.shelenkov.portfolio.service.registration.RegistrationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GoogleOAuth2LoginProcessor extends AbstractOAuth2LoginProcessor {

    private final RegistrationService registrationService;

    public GoogleOAuth2LoginProcessor(AccountService accountService,
                                      ClientRegistrationRepository clientRegistrationRepository,
                                      RegistrationService registrationService) {
        super(accountService, clientRegistrationRepository);
        this.registrationService = registrationService;
    }

    @Override
    public String getSupportedRegistrationId() {
        return CommonOAuth2Provider.GOOGLE.name();
    }

    @Override
    protected Account loadUserByOauth2Id(String oauth2Id) {
        return getAccountService().getByGoogleId(oauth2Id);
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
