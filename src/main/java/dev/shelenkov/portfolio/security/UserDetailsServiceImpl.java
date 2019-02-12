package dev.shelenkov.portfolio.security;

import dev.shelenkov.portfolio.model.Account;
import dev.shelenkov.portfolio.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.getByEmailWithRoles(email);
        if (account == null) {
            throw new UsernameNotFoundException(email + " not found");
        }
        return new ExtendedUser(
            email,
            account.getPassword(),
            account.isEnabled(),
            true,
            true,
            true,
            generateAuthoritiesList(account),
            account.getUsername());
    }

    private static Collection<? extends GrantedAuthority> generateAuthoritiesList(Account account) {
        return account.getRoles().stream()
            .map(e -> new SimpleGrantedAuthority(e.getName()))
            .collect(Collectors.toList());
    }
}
