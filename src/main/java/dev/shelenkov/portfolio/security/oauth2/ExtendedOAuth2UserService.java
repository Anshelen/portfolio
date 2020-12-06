package dev.shelenkov.portfolio.security.oauth2;

import dev.shelenkov.portfolio.security.ExtendedUser;
import dev.shelenkov.portfolio.security.oauth2.loginprocessors.OAuth2LoginProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequestEntityConverter;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static dev.shelenkov.portfolio.security.oauth2.OAuth2ErrorHelper.throwOnCaughtOAuth2Exception;
import static dev.shelenkov.portfolio.security.oauth2.OAuth2ErrorHelper.throwOnNoUriSpecified;
import static dev.shelenkov.portfolio.security.oauth2.OAuth2ErrorHelper.throwOnNoUserNameAttributeSpecified;
import static dev.shelenkov.portfolio.security.oauth2.OAuth2ErrorHelper.throwOnNotSupportedProviderException;
import static dev.shelenkov.portfolio.security.oauth2.OAuth2ErrorHelper.throwOnRestClientException;

@Component
@Slf4j
public class ExtendedOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final Converter<OAuth2UserRequest, RequestEntity<?>> requestEntityConverter
        = new OAuth2UserRequestEntityConverter();

    private final Collection<OAuth2LoginProcessor> loginProcessors;
    private final RestOperations restOperations;

    public ExtendedOAuth2UserService(Collection<OAuth2LoginProcessor> loginProcessors) {
        this.loginProcessors = Collections.unmodifiableCollection(loginProcessors);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        this.restOperations = restTemplate;
    }

    @SuppressWarnings("FeatureEnvy")
    @Override
    public ExtendedUser loadUser(OAuth2UserRequest userRequest) {
        Assert.notNull(userRequest, "userRequest cannot be null");

        ClientRegistration registration = userRequest.getClientRegistration();
        String uri = registration.getProviderDetails().getUserInfoEndpoint().getUri();
        String userNameAttributeName
            = registration.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        if (!StringUtils.hasText(uri)) {
            return throwOnNoUriSpecified(registration);
        }
        if (!StringUtils.hasText(userNameAttributeName)) {
            return throwOnNoUserNameAttributeSpecified(registration);
        }

        try {
            RequestEntity<?> request = requestEntityConverter.convert(userRequest);
            //noinspection ConstantConditions,AnonymousInnerClassMayBeStatic,AnonymousInnerClass
            ResponseEntity<Map<String, Object>> response
                = restOperations.exchange(request, new ParameterizedTypeReference<Map<String, Object>>() {});

            Map<String, Object> userAttributes = response.getBody();
            String provider = registration.getClientName();

            for (OAuth2LoginProcessor processor : loginProcessors) {
                if (processor.isRegistrationIdSupported(provider)) {
                    return processor.processUser(userAttributes, userRequest.getAccessToken().getTokenValue());
                }
            }

            return throwOnNotSupportedProviderException(registration);
        } catch (OAuth2AuthorizationException ex) {
            return throwOnCaughtOAuth2Exception(registration, ex);
        } catch (RestClientException ex) {
            return throwOnRestClientException(ex);
        }
    }
}
