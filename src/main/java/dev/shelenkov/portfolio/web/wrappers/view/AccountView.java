package dev.shelenkov.portfolio.web.wrappers.view;

import dev.shelenkov.portfolio.model.Account;
import dev.shelenkov.portfolio.model.Role;
import lombok.Data;

import java.util.Set;

@Data
public class AccountView {

    private final Long id;
    private final String username;
    private final String email;
    private final boolean enabled;
    private final Set<Role> roles;

    public AccountView(Account account) {
        this.id = account.getId();
        this.username = account.getUsername();
        this.email = account.getEmail();
        this.enabled = account.isEnabled();
        this.roles = account.getRoles();
    }
}
