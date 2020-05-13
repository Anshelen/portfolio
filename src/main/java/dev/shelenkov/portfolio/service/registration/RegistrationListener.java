package dev.shelenkov.portfolio.service.registration;

import dev.shelenkov.portfolio.model.Account;
import dev.shelenkov.portfolio.model.VerificationToken;
import dev.shelenkov.portfolio.repository.AccountRepository;
import dev.shelenkov.portfolio.repository.VerificationTokenRepository;
import dev.shelenkov.portfolio.service.mail.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

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

    private void confirmEmailAddress(Account account) {
        VerificationToken token = new VerificationToken(account);
        token = tokenRepository.save(token);
        emailService.sendConfirmationEmail(token);
    }
}
