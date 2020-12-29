package dev.shelenkov.portfolio.repository;

import dev.shelenkov.portfolio.domain.Account;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.QueryHint;

import static org.hibernate.annotations.QueryHints.PASS_DISTINCT_THROUGH;

public interface AccountRepository extends PagingAndSortingRepository<Account, Long> {

    boolean existsByEmail(String email);

    @QueryHints(@QueryHint(name = PASS_DISTINCT_THROUGH, value = "false"))
    @Query("select distinct a from Account a left join fetch a.loginCountries where a.id = :id")
    Account getByIdWithCountries(@Param("id") long id);

    Account getByEmail(String email);

    Account getByGithubId(String githubId);

    Account getByGoogleId(String googleId);
}
