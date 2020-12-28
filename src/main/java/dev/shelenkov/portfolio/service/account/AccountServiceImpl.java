package dev.shelenkov.portfolio.service.account;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public boolean existsByEmail(String email) {
        return accountRepository.existsByEmail(email);
    }

    @Override
    public Account getByIdWithCountries(long id) {
        return accountRepository.getByIdWithCountries(id);
    }

    @Override
    public Account getByEmail(String email) {
        return accountRepository.getByEmail(email);
    }

    @Override
    public Account getByGithubId(String githubId) {
        return accountRepository.getByGithubId(githubId);
    }

    @Override
    public Account getByGoogleId(String googleId) {
        return accountRepository.getByGoogleId(googleId);
    }

    @Override
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public Page<Account> findAll(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }
}
