package dev.shelenkov.portfolio.web.response;

import dev.shelenkov.portfolio.domain.Role;
import lombok.Value;

import java.util.Set;

@Value
public class AccountResponse {

    Long id;
    String username;
    String email;
    boolean enabled;
    Set<Role> roles;
}
