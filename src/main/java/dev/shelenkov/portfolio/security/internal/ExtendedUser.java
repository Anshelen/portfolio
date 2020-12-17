package dev.shelenkov.portfolio.security.internal;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.domain.Role;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.Validate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ExtendedUser extends User implements OAuth2User {

    private static final long serialVersionUID = -1;

    private final Map<String, Object> oauth2Attributes;

    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    private String name;

    public ExtendedUser(Account account) {
        this(account, new HashMap<>());
    }

    public ExtendedUser(Account account, Map<String, Object> oauth2Attributes) {
        super(account.getEmail(), account.getPassword(), account.isEnabled(),
            true,
            true,
            true,
            generateAuthoritiesList(account.getRoles()));
        this.id = Validate.notNull(account.getId());
        this.name = account.getUsername();
        this.oauth2Attributes = Collections.unmodifiableMap(new LinkedHashMap<>(oauth2Attributes));
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oauth2Attributes;
    }

    private static Collection<? extends GrantedAuthority> generateAuthoritiesList(
        Collection<Role> roles) {

        return roles.stream()
            .map(e -> new SimpleGrantedAuthority(e.getFullName()))
            .collect(Collectors.toList());
    }
}
