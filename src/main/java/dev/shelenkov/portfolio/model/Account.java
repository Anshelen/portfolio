package dev.shelenkov.portfolio.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Account extends AbstractPersistable<Long> {

    private String username;

    @Column(unique = true)
    private String email;

    private String password;

    private boolean enabled;

    @Column(name = "github_id")
    private String githubId;

    @Column(name = "google_id")
    private String googleId;

    @Setter(AccessLevel.NONE)
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Set<Role> roles = new HashSet<>();

    public Account(String username, String email, String password,
                   Iterable<? extends Role> roles) {
        this.username = username;
        this.email = email;
        this.password = password;
        roles.forEach(this::addRole);
    }

    public Account(String username, String email, String password, Role role) {
        this(username, email, password, Collections.singleton(role));
    }

    public void addRole(Role role) {
        roles.add(role);
        role.addAccount(this);
    }

    public void removeRole(Role role) {
        roles.remove(role);
        role.removeAccount(this);
    }

    public void clearRoles() {
        for (Role role : new ArrayList<>(roles)) {
            removeRole(role);
        }
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }
}
