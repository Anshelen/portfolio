package dev.shelenkov.portfolio.service.registration;

import dev.shelenkov.portfolio.model.Account;
import dev.shelenkov.portfolio.model.Role;
import dev.shelenkov.portfolio.model.VerificationToken;
import dev.shelenkov.portfolio.repository.AccountRepository;
import dev.shelenkov.portfolio.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.Validate;
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
@RequiredArgsConstructor
public class RegistrationService implements IRegistrationService {

    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final VerificationTokenRepository tokenRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public void registerNewUser(String userName, String email, String password) {
        if (accountRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                "There is an account with that email address: " + email);
        }

        Account account = new Account(
            userName,
            email,
            passwordEncoder.encode(password),
            Role.USER);
        accountRepository.save(account);
        sendConfirmationEmail(email);
    }

    @Transactional
    @Override
    public Account registerNewGitHubUser(String userName, String email, String githubId) {
        String password = getRandomPassword();

        Account account = new Account(
            userName,
            email,
            passwordEncoder.encode(password),
            Role.USER);
        account.setGithubId(githubId);
        account.setEnabled(true);

        accountRepository.save(account);
        return account;
    }

    @Transactional
    @Override
    public Account registerNewGoogleUser(String userName, String email, String googleId) {
        String password = getRandomPassword();

        Account account = new Account(
            userName,
            email,
            passwordEncoder.encode(password),
            Role.USER);
        account.setGoogleId(googleId);
        account.setEnabled(true);

        accountRepository.save(account);
        return account;
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

    @Transactional
    @Override
    public void sendConfirmationEmail(String email) {
        Validate.isTrue(canSendConfirmationEmail(email),
            "Confirmation email can't be sent");
        Account account = accountRepository.getByEmail(email);
        eventPublisher.publishEvent(
            new OnRegistrationCompleteEvent(this, account));
    }

    @Override
    public boolean canSendConfirmationEmail(String email) {
        Account account = accountRepository.getByEmail(email);
        return (account != null) && !account.isEnabled();
    }

    private static String getRandomPassword() {
        return RandomStringUtils.random(8);
    }
}
