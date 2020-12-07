package dev.shelenkov.portfolio.security.internal;

import dev.shelenkov.portfolio.security.exception.TooManyLoginAttemptsException;
import dev.shelenkov.portfolio.service.attempts.ILoginAttemptsAware;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * Improved {@link DaoAuthenticationProvider} that prevents brute force login attempts.
 */
@Slf4j
public final class ExtendedDaoAuthenticationProvider extends DaoAuthenticationProvider {

    private final ILoginAttemptsAware loginAttemptsAwareService;

    public ExtendedDaoAuthenticationProvider(ILoginAttemptsAware loginAttemptsAwareService,
                                             UserDetailsService userDetailsService,
                                             PasswordEncoder passwordEncoder) {
        this.loginAttemptsAwareService = loginAttemptsAwareService;
        setUserDetailsService(userDetailsService);
        setPasswordEncoder(passwordEncoder);
    }

    @SuppressWarnings("FeatureEnvy")
    @Override
    public Authentication authenticate(Authentication authentication) {
        String ip = ((WebAuthenticationDetails) authentication.getDetails()).getRemoteAddress();
        if (loginAttemptsAwareService.areTooManyFailedLoginAttempts(ip)) {
            log.warn("Brute force attempt from ip: {}. Login: {}", ip, authentication.getPrincipal());
            throw new TooManyLoginAttemptsException(ip);
        }

        try {
            Authentication result = super.authenticate(authentication);
            loginAttemptsAwareService.registerSuccessfulLogin(ip);
            return result;
        } catch (AuthenticationException e) {
            loginAttemptsAwareService.registerFailedLogin(ip);
            throw e;
        }
    }
}
