package dev.shelenkov.portfolio.repository;

import dev.shelenkov.portfolio.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByEmail(String email);

    Account getByEmail(String email);

    Account getByGithubId(String githubId);

    Account getByGoogleId(String googleId);
}
