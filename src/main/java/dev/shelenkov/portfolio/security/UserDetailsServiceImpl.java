package dev.shelenkov.portfolio.security;

import dev.shelenkov.portfolio.model.Account;
import dev.shelenkov.portfolio.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl extends AbstractExtendedUserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    protected Account getAccountByEmail(String email) {
        return accountRepository.getByEmailWithRoles(email);
    }
}
