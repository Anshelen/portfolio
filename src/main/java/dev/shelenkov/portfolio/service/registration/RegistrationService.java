package dev.shelenkov.portfolio.service.registration;

import dev.shelenkov.portfolio.model.Account;
import dev.shelenkov.portfolio.model.Role;
import dev.shelenkov.portfolio.repository.AccountRepository;
import dev.shelenkov.portfolio.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Basic implementation of service for registering new users.
 */
@Service
public class RegistrationService implements IRegistrationService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    @Override
    public void registerNewUser(String userName, String email, String password) {
        if (accountRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                "There is an account with that email address: " + email);
        }

        Role role = roleRepository.getByName("ROLE_USER");
        Account account = new Account(
            userName,
            email,
            passwordEncoder.encode(password),
            role);
        accountRepository.save(account);
    }
}
