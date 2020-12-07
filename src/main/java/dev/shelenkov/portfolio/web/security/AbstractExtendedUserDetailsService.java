package dev.shelenkov.portfolio.web.security;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.web.support.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
public abstract class AbstractExtendedUserDetailsService implements UserDetailsService {

    @SuppressWarnings("FeatureEnvy")
    @Override
    public UserDetails loadUserByUsername(String email) {
        Account account = getAccountByEmail(email);
        if (account == null) {
            log.debug("User not found for email: {}", email);
            throw new UsernameNotFoundException(email + " not found");
        }
        return new ExtendedUser(
            email,
            account.getPassword(),
            account.isEnabled(),
            SecurityUtils.generateAuthoritiesList(account),
            account.getUsername());
    }

    protected abstract Account getAccountByEmail(String email);
}
