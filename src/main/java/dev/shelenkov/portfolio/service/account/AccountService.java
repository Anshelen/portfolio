package dev.shelenkov.portfolio.service.account;

import dev.shelenkov.portfolio.domain.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountService {

    boolean existsByEmail(String email);

    Account getByEmail(String email);

    Account getByGithubId(String githubId);

    Account getByGoogleId(String googleId);

    Account save(Account account);

    Page<Account> findAll(Pageable pageable);
}
