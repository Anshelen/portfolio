package dev.shelenkov.portfolio.repository;

import dev.shelenkov.portfolio.model.Account;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AccountRepository extends PagingAndSortingRepository<Account, Long> {

    boolean existsByEmail(String email);

    Account getByEmail(String email);

    Account getByGithubId(String githubId);

    Account getByGoogleId(String googleId);
}
