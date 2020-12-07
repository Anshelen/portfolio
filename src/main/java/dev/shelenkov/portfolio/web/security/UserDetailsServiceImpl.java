package dev.shelenkov.portfolio.web.security;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl extends AbstractExtendedUserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    protected Account getAccountByEmail(String email) {
        return accountRepository.getByEmail(email);
    }
}
