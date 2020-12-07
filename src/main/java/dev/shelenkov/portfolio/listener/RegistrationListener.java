package dev.shelenkov.portfolio.listener;

import dev.shelenkov.portfolio.domain.RegistrationMethod;
import dev.shelenkov.portfolio.event.AccountRegisteredEvent;
import dev.shelenkov.portfolio.service.registration.ConfirmEmailService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RegistrationListener implements ApplicationListener<AccountRegisteredEvent> {

    private final ConfirmEmailService confirmEmailService;

    @SneakyThrows(IOException.class)
    @Override
    public void onApplicationEvent(AccountRegisteredEvent event) {
        if (event.getRegistrationMethod() == RegistrationMethod.EMAIL) {
            confirmEmailService.sendConfirmationEmail(event.getAccountId());
        }
    }
}
