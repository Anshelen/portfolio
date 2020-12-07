package dev.shelenkov.portfolio.web.security.oauth2.loginprocessors;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.repository.AccountRepository;
import dev.shelenkov.portfolio.service.registration.IRegistrationService;
import lombok.Data;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class GitHubOAuth2LoginProcessor extends AbstractOAuth2LoginProcessor {

    private static final String GET_EMAIL_URL = "https://api.github.com/user/emails";

    private final IRegistrationService registrationService;
    private final RestOperations restOperations;

    public GitHubOAuth2LoginProcessor(AccountRepository accountRepository,
                                      ClientRegistrationRepository clientRegistrationRepository,
                                      IRegistrationService registrationService) {
        super(accountRepository, clientRegistrationRepository);
        this.registrationService = registrationService;
        this.restOperations = new RestTemplate();
    }

    @Override
    public String getSupportedRegistrationId() {
        return CommonOAuth2Provider.GITHUB.name();
    }

    @Override
    protected Account loadUserByOauth2Id(String oauth2Id) {
        return getAccountRepository().getByGithubId(oauth2Id);
    }

    @Override
    protected Account registerNewAccount(Map<String, Object> userAttributes,
                                         String email,
                                         String oauth2Id) {
        String username = (String) userAttributes.get("login");
        return registrationService.registerNewGitHubUser(username, email, oauth2Id);
    }

    @Override
    protected void setOAuth2IdForAccount(Account account, String oauth2Id) {
        account.setGithubId(oauth2Id);
    }

    @Override
    protected String getVerifiedEmail(Map<String, Object> userAttributes, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        //noinspection AnonymousInnerClassMayBeStatic,AnonymousInnerClass
        ResponseEntity<List<GitHubOAuth2LoginProcessor.GitHubEmail>> emailResponse
            = restOperations.exchange(
            GET_EMAIL_URL, HttpMethod.GET, new HttpEntity<>(headers),
            new ParameterizedTypeReference<List<GitHubOAuth2LoginProcessor.GitHubEmail>>() {});
        /*
         * Response example:
         * [{"email":"anshelen@yandex.ru","primary":true,"verified":true,"visibility":"private"},
         * {"email":"32411111+Anshelen@users.noreply.github.com","primary":false,"verified":true,"visibility":null}]
         */
        List<GitHubOAuth2LoginProcessor.GitHubEmail> emails = emailResponse.getBody();
        if (emails == null) {
            throw new RuntimeException("Not expected response from OAuth2 provider: " + emailResponse);
        }
        return emails.stream()
            .filter(e -> e.isPrimary() && e.isVerified())
            .findFirst().map(GitHubOAuth2LoginProcessor.GitHubEmail::getEmail).orElse(null);
    }

    @Data
    private static class GitHubEmail {
        private String email;
        private boolean primary;
        private boolean verified;
        private String visibility;
    }
}
