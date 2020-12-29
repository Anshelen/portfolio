package dev.shelenkov.portfolio.listener;

import dev.shelenkov.portfolio.event.SuspiciousLocationLoginEvent;
import dev.shelenkov.portfolio.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Validated
@Slf4j
public class SuspiciousLocationLoginListener {

    private final EmailService emailService;

    @SuppressWarnings("FeatureEnvy")
    @Async
    @EventListener
    public void onSuspiciousLocationLoginEvent(
        @Valid SuspiciousLocationLoginEvent event) throws IOException {

        log.debug("Caught suspicious location login event: {}", event);
        emailService.sendSuspiciousLocationEmail(
            event.getAccount(), event.getIp(), event.getCountry());
    }
}
