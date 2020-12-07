package dev.shelenkov.portfolio.security;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.security.internal.ExtendedUser;
import dev.shelenkov.portfolio.security.support.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class SecurityOperations implements ISecurityOperations {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public void loginAccount(Account account) {
        Collection<? extends GrantedAuthority> authorities
            = SecurityUtils.generateAuthoritiesList(account);
        ExtendedUser user = new ExtendedUser(
            account.getEmail(),
            account.getPassword(),
            account.isEnabled(),
            authorities,
            account.getUsername());
        user.eraseCredentials();
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
