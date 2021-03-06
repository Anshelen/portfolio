package dev.shelenkov.portfolio.support;

import dev.shelenkov.portfolio.publisher.EventsPublisherImpl;
import dev.shelenkov.portfolio.security.config.AuthenticationConfig;
import dev.shelenkov.portfolio.security.config.AuthorizationConfig;
import dev.shelenkov.portfolio.security.config.CorsConfig;
import dev.shelenkov.portfolio.security.config.SameSiteCookieConfig;
import dev.shelenkov.portfolio.security.config.SecurityProperties;
import dev.shelenkov.portfolio.service.attempts.LoginAttemptsAware;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@TestConfiguration
@EnableConfigurationProperties(SecurityProperties.class)
@Import({
    AuthenticationConfig.class,
    AuthorizationConfig.class,
    CorsConfig.class,
    SameSiteCookieConfig.class,
    EventsPublisherImpl.class
})
public class SpringSecurityTestConfig {

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        return new MockUserDetailsServiceImpl();
    }

    @Bean
    @Primary
    public LoginAttemptsAware requestAttemptsService() {
        return new MockLoginAttemptsAwareService();
    }

    @Bean
    @Primary
    public PersistentTokenRepository tokenRepository() {
        return new InMemoryTokenRepositoryImpl();
    }
}
