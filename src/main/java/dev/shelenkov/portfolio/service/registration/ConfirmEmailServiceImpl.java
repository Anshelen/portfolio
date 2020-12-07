package dev.shelenkov.portfolio.service.registration;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.domain.VerificationToken;
import dev.shelenkov.portfolio.repository.AccountRepository;
import dev.shelenkov.portfolio.repository.VerificationTokenRepository;
import dev.shelenkov.portfolio.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional
public class ConfirmEmailServiceImpl implements ConfirmEmailService {

    private final AccountRepository accountRepository;
    private final VerificationTokenRepository tokenRepository;
    private final EmailService emailService;

    @Async
    @Override
    public void sendConfirmationEmail(long accountId) throws IOException {
        Account account = accountRepository.findById(accountId).orElse(null);
        Validate.validState(account != null);
        Validate.validState(!account.isEnabled());

        VerificationToken token = new VerificationToken(account);
        tokenRepository.save(token);
        emailService.sendConfirmationEmail(token);
    }

    @Async
    @Override
    public void sendConfirmationEmail(String email) throws IOException {
        Account account = accountRepository.getByEmail(email);
        Validate.validState(account != null);
        Validate.validState(!account.isEnabled());

        VerificationToken token = new VerificationToken(account);
        tokenRepository.save(token);
        emailService.sendConfirmationEmail(token);
    }

    @Override
    public boolean isSendConfirmationEmailForbidden(String email) {
        Account account = accountRepository.getByEmail(email);
        return (account == null) || account.isEnabled();
    }
}
