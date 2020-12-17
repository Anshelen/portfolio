package dev.shelenkov.portfolio.security;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.security.internal.ExtendedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityOperationsImpl implements SecurityOperations {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public void loginAccount(Account account) {
        ExtendedUser user = new ExtendedUser(account);
        user.eraseCredentials();
        Authentication authentication
            = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
