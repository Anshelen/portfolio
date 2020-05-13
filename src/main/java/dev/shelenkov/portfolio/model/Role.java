package dev.shelenkov.portfolio.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
public class Role extends AbstractPersistable<Long> {

    @Getter
    @Setter
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<Account> accounts = new HashSet<>();

    public Role(String name) {
        this.name = name;
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public void removeAccount(Account account) {
        accounts.remove(account);
    }

    public Set<Account> getAccounts() {
        return Collections.unmodifiableSet(accounts);
    }
}
