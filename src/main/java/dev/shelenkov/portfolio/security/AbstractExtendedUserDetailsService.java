package dev.shelenkov.portfolio.security;

import dev.shelenkov.portfolio.model.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.stream.Collectors;

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
            generateAuthoritiesList(account),
            account.getUsername());
    }

    protected abstract Account getAccountByEmail(String email);

    private static Collection<? extends GrantedAuthority> generateAuthoritiesList(Account account) {
        return account.getRoles().stream()
            .map(e -> new SimpleGrantedAuthority(e.getFullName()))
            .collect(Collectors.toList());
    }
}
