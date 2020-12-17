package dev.shelenkov.portfolio.security.internal;

import dev.shelenkov.portfolio.domain.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
public abstract class AbstractExtendedUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String email) {
        Account account = getAccountByEmail(email);
        if (account == null) {
            log.debug("User not found for email: {}", email);
            throw new UsernameNotFoundException(email + " not found");
        }
        return new ExtendedUser(account);
    }

    protected abstract Account getAccountByEmail(String email);
}
