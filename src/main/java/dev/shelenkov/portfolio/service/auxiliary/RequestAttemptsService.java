package dev.shelenkov.portfolio.service.auxiliary;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Service for limiting a number of important requests sent by a single user.
 */
@Service
@Slf4j
public class RequestAttemptsService {

    private final int maxResendConfirmationEmailAttempts;

    public RequestAttemptsService(
        @Value("${max_attempts.resend_confirmation_email:3}") int maxResendConfirmationEmailAttempts) {

        this.maxResendConfirmationEmailAttempts = maxResendConfirmationEmailAttempts;
    }

    @SuppressWarnings("AnonymousInnerClass")
    private final LoadingCache<String, Integer> confirmationEmailAttemptsCache
        = CacheBuilder.newBuilder()
        .expireAfterWrite(1, TimeUnit.DAYS)
        .build(new CacheLoader<String, Integer>() {
            public Integer load(String key) {
                return 0;
            }
        });

    public void registerConfirmationEmailResent(@NonNull String ip) {
        try {
            int attempts = confirmationEmailAttemptsCache.get(ip);
            attempts++;
            confirmationEmailAttemptsCache.put(ip, attempts);
        } catch (ExecutionException e) {
            log.error("registerConfirmationEmailResent. Error", e);
        }
    }

    public boolean areTooManyConfirmationEmailsResent(@Nullable String ip) {
        try {
            if (ip == null) {
                log.warn("areTooManyConfirmationEmailsResent. Null IP");
                return true;
            }
            return confirmationEmailAttemptsCache.get(ip) >= maxResendConfirmationEmailAttempts;
        } catch (ExecutionException e) {
            log.error("areTooManyConfirmationEmailsResent. Error", e);
            return false;
        }
    }
}
