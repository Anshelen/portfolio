package dev.shelenkov.portfolio.web.response;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.domain.Role;
import lombok.Data;

import java.util.Set;

@Data
public class AccountResponse {

    private final Long id;
    private final String username;
    private final String email;
    private final boolean enabled;
    private final Set<Role> roles;

    public AccountResponse(Account account) {
        this.id = account.getId();
        this.username = account.getUsername();
        this.email = account.getEmail();
        this.enabled = account.isEnabled();
        this.roles = account.getRoles();
    }
}
