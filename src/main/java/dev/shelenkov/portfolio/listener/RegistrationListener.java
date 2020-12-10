package dev.shelenkov.portfolio.listener;

import dev.shelenkov.portfolio.domain.RegistrationMethod;
import dev.shelenkov.portfolio.event.AccountRegisteredEvent;
import dev.shelenkov.portfolio.service.registration.ConfirmEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Validated
public class RegistrationListener {

    private final ConfirmEmailService confirmEmailService;

    @Async
    @TransactionalEventListener
    public void onApplicationEvent(@Valid AccountRegisteredEvent event) throws IOException {
        if (event.getRegistrationMethod() == RegistrationMethod.EMAIL) {
            confirmEmailService.sendConfirmationEmail(event.getAccountId());
        }
    }
}
