package dev.shelenkov.portfolio.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq_generator")
    @SequenceGenerator(
        name = "account_seq_generator",
        sequenceName = "account_id_seq",
        allocationSize = 10)
    private Long id;

    private String username;

    @Column(unique = true)
    private String email;

    @ToString.Exclude
    private String password;

    @Version
    private int version;

    private boolean enabled;

    @Column(name = "github_id")
    private String githubId;

    @Column(name = "google_id")
    private String googleId;

    @Setter(AccessLevel.NONE)
    @Type(type = "dev.shelenkov.portfolio.repository.support.SetOfRolesType")
    @Column(columnDefinition = "user_role[]")
    private Set<Role> roles = EnumSet.noneOf(Role.class);

    /**
     * Countries from which user tried to log in.
     */
    @ToString.Exclude
    @Setter(AccessLevel.NONE)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "account_x_country",
        joinColumns = @JoinColumn(name = "account_id"),
        inverseJoinColumns = @JoinColumn(name = "country_id")
    )
    private Set<Country> loginCountries = new HashSet<>();

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

    public void addLoginCountry(Country country) {
        loginCountries.add(country);
    }

    public void removeLoginCountry(Country country) {
        loginCountries.remove(country);
    }

    public boolean containsLoginCountry(Country country) {
        return loginCountries.contains(country);
    }

    public Set<Country> getLoginCountries() {
        return Collections.unmodifiableSet(loginCountries);
    }
}
