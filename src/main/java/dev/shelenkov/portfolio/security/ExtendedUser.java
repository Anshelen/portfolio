package dev.shelenkov.portfolio.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExtendedUser extends User implements OAuth2User {

    private static final long serialVersionUID = -1;

    private final Map<String, Object> oauth2Attributes;

    @Getter
    @Setter
    private String name;

    public ExtendedUser(String email, String password, boolean enabled,
                        Collection<? extends GrantedAuthority> authorities,
                        String name) {
        super(email, password, enabled,
            true,
            true,
            true,
            authorities);
        this.name = name;
        this.oauth2Attributes = Collections.emptyMap();
    }

    public ExtendedUser(String email, String password, boolean enabled,
                        Collection<? extends GrantedAuthority> authorities,
                        String name, Map<String, Object> oauth2Attributes) {
        super(email, password, enabled,
            true,
            true,
            true,
            authorities);
        this.name = name;
        this.oauth2Attributes = Collections.unmodifiableMap(new LinkedHashMap<>(oauth2Attributes));
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oauth2Attributes;
    }
}
