package dev.shelenkov.portfolio.security;

import dev.shelenkov.portfolio.model.Account;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

@UtilityClass
public class SecurityUtils {

    public static Collection<? extends GrantedAuthority> generateAuthoritiesList(Account account) {
        return account.getRoles().stream()
            .map(e -> new SimpleGrantedAuthority(e.getFullName()))
            .collect(Collectors.toList());
    }
}
