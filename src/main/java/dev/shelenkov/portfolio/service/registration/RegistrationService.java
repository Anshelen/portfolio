package dev.shelenkov.portfolio.service.registration;

import dev.shelenkov.portfolio.model.Account;
import dev.shelenkov.portfolio.model.Role;
import dev.shelenkov.portfolio.model.VerificationToken;
import dev.shelenkov.portfolio.repository.AccountRepository;
import dev.shelenkov.portfolio.repository.RoleRepository;
import dev.shelenkov.portfolio.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

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

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

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
        eventPublisher.publishEvent(
            new OnRegistrationCompleteEvent(this, account));
    }

    /**
     * @throws TokenExpiredException in case of token is expired
     */
    @Override
    public Account confirmRegistration(UUID token) throws TokenNotValidException {

        Optional<VerificationToken> verificationTokenOpt = tokenRepository.findById(token);
        if (!verificationTokenOpt.isPresent()) {
            throw new TokenNotValidException();
        }

        VerificationToken verificationToken = verificationTokenOpt.get();
        Account account = verificationToken.getAccount();
        if (account.isEnabled()) {
            throw new TokenNotValidException();
        }
        if (verificationToken.getExpirationDate().isBefore(Instant.now())) {
            throw new TokenExpiredException();
        }

        account.setEnabled(true);
        return accountRepository.save(account);
    }
}
