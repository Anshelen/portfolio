package dev.shelenkov.portfolio.security.config;

import dev.shelenkov.portfolio.publisher.EventsPublisher;
import dev.shelenkov.portfolio.security.exception.TooManyLoginAttemptsException;
import dev.shelenkov.portfolio.security.internal.ExtendedDaoAuthenticationProvider;
import dev.shelenkov.portfolio.security.internal.ExtendedSuccessAuthenticationHandler;
import dev.shelenkov.portfolio.security.internal.OverridingIpWebAuthenticationDetailsSource;
import dev.shelenkov.portfolio.security.internal.ParametersMappingAuthenticationFailureHandler;
import dev.shelenkov.portfolio.security.oauth2.OAuth2NoVerifiedEmailException;
import dev.shelenkov.portfolio.service.attempts.LoginAttemptsAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AuthenticationConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ParametersMappingAuthenticationFailureHandler authenticationFailureHandler() {
        ParametersMappingAuthenticationFailureHandler handler
            = new ParametersMappingAuthenticationFailureHandler();
        Map<String, String> map = new HashMap<>();
        map.put(BadCredentialsException.class.getName(),
            "/login?error=BadCredentials");
        map.put(CredentialsExpiredException.class.getName(),
            "/login?error=CredentialsExpired");
        map.put(LockedException.class.getName(),
            "/login?error=Locked");
        map.put(DisabledException.class.getName(),
            "/login?error=Disabled&email=${email}");
        map.put(OAuth2NoVerifiedEmailException.class.getName(),
            "/login?error=NoVerifiedEmail");
        map.put(TooManyLoginAttemptsException.class.getName(),
            "/login?error=TooManyAttempts");
        handler.setExceptionMappings(map);
        return handler;
    }

    @Bean
    public WebAuthenticationDetailsSource webAuthenticationDetailsSource() {
        return new OverridingIpWebAuthenticationDetailsSource();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler(EventsPublisher eventsPublisher) {
        return new ExtendedSuccessAuthenticationHandler(eventsPublisher);
    }

    @Bean
    public ExtendedDaoAuthenticationProvider daoAuthenticationProvider(
        UserDetailsService userDetailsService, LoginAttemptsAware loginAttemptsAwareService) {

        return new ExtendedDaoAuthenticationProvider(
            loginAttemptsAwareService, userDetailsService, passwordEncoder());
    }
}
