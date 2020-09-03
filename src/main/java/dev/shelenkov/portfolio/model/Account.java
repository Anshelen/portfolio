package dev.shelenkov.portfolio.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
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
    @Type(type = "dev.shelenkov.portfolio.model.utils.SetOfRolesType")
    @Column(columnDefinition = "user_role[]")
    private Set<Role> roles = EnumSet.noneOf(Role.class);

    public Account(String username, String email, String password,
                   Collection<Role> roles) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = EnumSet.copyOf(roles);
    }

    public Account(String username, String email, String password, Role role) {
        this(username, email, password, Collections.singleton(role));
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public void removeRole(Role role) {
        roles.remove(role);
    }

    public void clearRoles() {
        roles.clear();
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }
}
