package dev.shelenkov.portfolio.repository;

import dev.shelenkov.portfolio.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("FROM Account a JOIN FETCH a.roles WHERE a.email = :email")
    Account getByEmailWithRoles(@Param("email") String email);

    Account getByEmail(String email);

    boolean existsByEmail(String email);

    @Query("FROM Account a JOIN FETCH a.roles WHERE a.githubId = :githubId")
    Account getByGithubIdWithRoles(@Param("githubId") String githubId);

    @Query("FROM Account a JOIN FETCH a.roles WHERE a.googleId = :googleId")
    Account getByGoogleIdWithRoles(@Param("googleId") String googleId);
}
