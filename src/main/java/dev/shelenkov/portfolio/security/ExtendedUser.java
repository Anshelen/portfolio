package dev.shelenkov.portfolio.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class ExtendedUser extends User {

    private static final long serialVersionUID = -1;

    @Getter
    @Setter
    private String name;

    ExtendedUser(String email, String password, boolean enabled,
                 Collection<? extends GrantedAuthority> authorities,
                 String name) {
        super(email, password, enabled,
            true,
            true,
            true,
            authorities);
        this.name = name;
    }
}
