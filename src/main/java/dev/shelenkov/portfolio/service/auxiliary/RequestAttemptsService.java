package dev.shelenkov.portfolio.service.auxiliary;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Service for limiting a number of important requests sent by a single user.
 */
@Service
@Slf4j
public class RequestAttemptsService implements ILoginAttemptsAware, IResendConfirmationEmailAttemptsAware {

    private final int maxResendConfirmationEmailAttempts;
    private final int maxLoginAttempts;

    @SuppressWarnings({"AnonymousInnerClass", "AnonymousInnerClassMayBeStatic"})
    private final LoadingCache<String, Integer> confirmationEmailAttemptsCache
        = CacheBuilder.newBuilder()
        .expireAfterWrite(1, TimeUnit.DAYS)
        .build(new CacheLoader<String, Integer>() {
            public Integer load(@NonNull String key) {
                return 0;
            }
        });

    @SuppressWarnings({"AnonymousInnerClass", "AnonymousInnerClassMayBeStatic"})
    private final LoadingCache<String, Integer> loginAttemptsCache
        = CacheBuilder.newBuilder()
        .expireAfterWrite(1, TimeUnit.DAYS)
        .build(new CacheLoader<String, Integer>() {
            public Integer load(@NonNull String key) {
                return 0;
            }
        });

    public RequestAttemptsService(
        @Value("${max_attempts.resend_confirmation_email:3}") int maxResendConfirmationEmailAttempts,
        @Value("${max_attempts.login:3}") int maxLoginAttempts) {

        this.maxResendConfirmationEmailAttempts = maxResendConfirmationEmailAttempts;
        this.maxLoginAttempts = maxLoginAttempts;
    }

    @Override
    public void registerConfirmationEmailResent(@NonNull String ip) {
        try {
            int attempts = confirmationEmailAttemptsCache.get(ip);
            attempts++;
            confirmationEmailAttemptsCache.put(ip, attempts);
        } catch (ExecutionException e) {
            log.error("registerConfirmationEmailResent. Error", e);
        }
    }

    @Override
    public boolean areTooManyConfirmationEmailsResent(@NonNull String ip) {
        try {
            return confirmationEmailAttemptsCache.get(ip) >= maxResendConfirmationEmailAttempts;
        } catch (ExecutionException e) {
            log.error("areTooManyConfirmationEmailsResent. Error", e);
            return false;
        }
    }

    @Override
    public void registerSuccessfulLogin(@NonNull String ip) {
        loginAttemptsCache.invalidate(ip);
    }

    @Override
    public void registerFailedLogin(@NonNull String ip) {
        try {
            int attempts = loginAttemptsCache.get(ip);
            attempts++;
            loginAttemptsCache.put(ip, attempts);
        } catch (ExecutionException e) {
            log.error("registerFailedLogin. Error", e);
        }
    }

    @Override
    public boolean areTooManyFailedLoginAttempts(@NonNull String ip) {
        try {
            return loginAttemptsCache.get(ip) >= maxLoginAttempts;
        } catch (ExecutionException e) {
            log.error("areTooManyFailedLoginAttempts. Error", e);
            return false;
        }
    }
}
