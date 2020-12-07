package dev.shelenkov.portfolio.service.registration;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.domain.RegistrationMethod;
import dev.shelenkov.portfolio.domain.Role;
import dev.shelenkov.portfolio.domain.VerificationToken;
import dev.shelenkov.portfolio.publisher.EventsPublisher;
import dev.shelenkov.portfolio.repository.AccountRepository;
import dev.shelenkov.portfolio.repository.VerificationTokenRepository;
import dev.shelenkov.portfolio.security.SecurityOperations;
import dev.shelenkov.portfolio.service.exception.TokenExpiredException;
import dev.shelenkov.portfolio.service.exception.TokenNotValidException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
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
@Transactional
public class RegistrationServiceImpl implements RegistrationService {

    private final SecurityOperations securityOperations;
    private final AccountRepository accountRepository;
    private final VerificationTokenRepository tokenRepository;
    private final EventsPublisher eventsPublisher;

    @Override
    public void registerNewUser(String userName, String email, String password) {
        if (accountRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                "There is an account with that email address: " + email);
        }

        Account account = new Account(
            userName,
            email,
            securityOperations.encryptPassword(password),
            Role.USER);
        accountRepository.save(account);

        eventsPublisher.accountRegistered(account, RegistrationMethod.EMAIL);
    }

    @Override
    public Account registerNewGitHubUser(String userName, String email, String githubId) {
        String password = getRandomPassword();

        Account account = new Account(
            userName,
            email,
            securityOperations.encryptPassword(password),
            Role.USER);
        account.setGithubId(githubId);
        account.setEnabled(true);

        accountRepository.save(account);

        eventsPublisher.accountRegistered(account, RegistrationMethod.GITHUB);

        return account;
    }

    @Override
    public Account registerNewGoogleUser(String userName, String email, String googleId) {
        String password = getRandomPassword();

        Account account = new Account(
            userName,
            email,
            securityOperations.encryptPassword(password),
            Role.USER);
        account.setGoogleId(googleId);
        account.setEnabled(true);

        accountRepository.save(account);

        eventsPublisher.accountRegistered(account, RegistrationMethod.GOOGLE);

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

        securityOperations.loginAccount(account);

        return accountRepository.save(account);
    }

    private static String getRandomPassword() {
        return RandomStringUtils.random(8);
    }
}
