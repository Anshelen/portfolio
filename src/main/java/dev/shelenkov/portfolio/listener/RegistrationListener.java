package dev.shelenkov.portfolio.listener;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.domain.VerificationToken;
import dev.shelenkov.portfolio.event.OnRegistrationCompleteEvent;
import dev.shelenkov.portfolio.mail.EmailService;
import dev.shelenkov.portfolio.repository.AccountRepository;
import dev.shelenkov.portfolio.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RegistrationListener
    implements ApplicationListener<OnRegistrationCompleteEvent> {

    private final EmailService emailService;
    private final VerificationTokenRepository tokenRepository;
    private final AccountRepository accountRepository;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        Account account = accountRepository.findById(event.getAccountId())
            .orElseThrow(() -> new RuntimeException("Account to be confirmed doesn't exist"));
        confirmEmailAddress(account);
    }

    @SneakyThrows(IOException.class)
    private void confirmEmailAddress(Account account) {
        VerificationToken token = new VerificationToken(account);
        token = tokenRepository.save(token);
        emailService.sendConfirmationEmail(token);
    }
}
