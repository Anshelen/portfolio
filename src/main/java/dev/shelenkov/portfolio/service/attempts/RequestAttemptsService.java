package dev.shelenkov.portfolio.service.attempts;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import dev.shelenkov.portfolio.service.config.MaxAttemptsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Service for limiting a number of important requests sent by a single user.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RequestAttemptsService
    implements LoginAttemptsAware, ResendConfirmationEmailAttemptsAware, SendEmailToAdminAttemptsAware {

    private final MaxAttemptsProperties maxAttemptsProperties;

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

    @SuppressWarnings({"AnonymousInnerClass", "AnonymousInnerClassMayBeStatic"})
    private final LoadingCache<String, Integer> sendEmailToAdminAttemptsCache
        = CacheBuilder.newBuilder()
        .expireAfterWrite(1, TimeUnit.DAYS)
        .build(new CacheLoader<String, Integer>() {
            public Integer load(@NonNull String key) {
                return 0;
            }
        });

    @Override
    public void registerConfirmationEmailResent(String ip) {
        try {
            int attempts = confirmationEmailAttemptsCache.get(ip);
            attempts++;
            confirmationEmailAttemptsCache.put(ip, attempts);
        } catch (ExecutionException e) {
            log.error("registerConfirmationEmailResent. Error", e);
        }
    }

    @Override
    public boolean areTooManyConfirmationEmailsResent(String ip) {
        try {
            return confirmationEmailAttemptsCache.get(ip) >= maxAttemptsProperties.getResendConfirmationEmail();
        } catch (ExecutionException e) {
            log.error("areTooManyConfirmationEmailsResent. Error", e);
            return false;
        }
    }

    @Override
    public void registerSuccessfulLogin(String ip) {
        loginAttemptsCache.invalidate(ip);
    }

    @Override
    public void registerFailedLogin(String ip) {
        try {
            int attempts = loginAttemptsCache.get(ip);
            attempts++;
            loginAttemptsCache.put(ip, attempts);
        } catch (ExecutionException e) {
            log.error("registerFailedLogin. Error", e);
        }
    }

    @Override
    public boolean areTooManyFailedLoginAttempts(String ip) {
        try {
            return loginAttemptsCache.get(ip) >= maxAttemptsProperties.getLogin();
        } catch (ExecutionException e) {
            log.error("areTooManyFailedLoginAttempts. Error", e);
            return false;
        }
    }

    @Override
    public void registerEmailToAdminSent(String ip) {
        try {
            int attempts = sendEmailToAdminAttemptsCache.get(ip);
            attempts++;
            sendEmailToAdminAttemptsCache.put(ip, attempts);
        } catch (ExecutionException e) {
            log.error("registerEmailToAdminSent. Error", e);
        }
    }

    @Override
    public boolean areTooManyEmailsToAdminSent(String ip) {
        try {
            return sendEmailToAdminAttemptsCache.get(ip) >= maxAttemptsProperties.getSendEmailToAdmin();
        } catch (ExecutionException e) {
            log.error("areTooManyEmailsToAdminSent. Error", e);
            return false;
        }
    }
}
