package dev.shelenkov.portfolio.listener;

import dev.shelenkov.portfolio.event.LoginEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Component
@RequiredArgsConstructor
@Validated
@Slf4j
public class LoginListener {

    @Async
    @EventListener
    @Transactional
    public void onLoginEvent(@Valid LoginEvent event) {

        log.debug("Processing login event: {}", event);

        String ip = event.getIp();
        long accountId = event.getAccountId();

        // TODO: 28.12.2020 main logic will be here
    }
}
