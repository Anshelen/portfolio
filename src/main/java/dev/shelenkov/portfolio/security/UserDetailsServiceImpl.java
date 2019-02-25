package dev.shelenkov.portfolio.security;

import dev.shelenkov.portfolio.model.Account;
import dev.shelenkov.portfolio.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl extends AbstractExtendedUserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    protected Account getAccountByEmail(String email) {
        return accountRepository.getByEmailWithRoles(email);
    }
}
