package dev.shelenkov.portfolio.security.internal;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.service.account.IAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl extends AbstractExtendedUserDetailsService {

    private final IAccountService accountService;

    @Override
    protected Account getAccountByEmail(String email) {
        return accountService.getByEmail(email);
    }
}
