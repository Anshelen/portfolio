package dev.shelenkov.portfolio.support;

import dev.shelenkov.portfolio.service.attempts.ILoginAttemptsAware;
import dev.shelenkov.portfolio.web.config.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@TestConfiguration
@EnableConfigurationProperties(SecurityProperties.class)
public class SpringSecurityTestConfig {

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        return new MockUserDetailsServiceImpl();
    }

    @Bean
    @Primary
    public ILoginAttemptsAware requestAttemptsService() {
        return new MockLoginAttemptsAwareService();
    }

    @Bean
    @Primary
    public PersistentTokenRepository tokenRepository() {
        return new InMemoryTokenRepositoryImpl();
    }
}
