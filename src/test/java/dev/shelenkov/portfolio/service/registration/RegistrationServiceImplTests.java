package dev.shelenkov.portfolio.service.registration;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.domain.RegistrationMethod;
import dev.shelenkov.portfolio.domain.Role;
import dev.shelenkov.portfolio.domain.VerificationToken;
import dev.shelenkov.portfolio.publisher.EventsPublisher;
import dev.shelenkov.portfolio.repository.AccountRepository;
import dev.shelenkov.portfolio.repository.VerificationTokenRepository;
import dev.shelenkov.portfolio.security.SecurityOperations;
import dev.shelenkov.portfolio.service.exception.TokenExpiredException;
import dev.shelenkov.portfolio.service.exception.TokenNotValidException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RegistrationServiceImpl.class)
@DisplayName("RegistrationService tests")
public class RegistrationServiceImplTests {

    private static final Long ACCOUNT_ID = 1L;
    private static final String EMAIL = "anshelen@yandex.ru";
    private static final String USERNAME = "name";
    private static final String PASSWORD = "password";
    private static final Role ROLE = Role.USER;
    private static final UUID TOKEN = UUID.randomUUID();

    @Autowired
    private RegistrationService registrationService;

    @MockBean
    private SecurityOperations securityOperations;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private VerificationTokenRepository tokenRepository;

    @MockBean
    private EventsPublisher eventsPublisher;

    @Captor
    private ArgumentCaptor<Account> captor;

    @AfterEach
    public void afterAll() {
        reset(accountRepository, tokenRepository, eventsPublisher, securityOperations);
    }

    @Nested
    @DisplayName("registerNewUser tests")
    class RegisterNewUserTests {

        @BeforeEach
        public void beforeEach() {
            Account newAccount = createDisabledAccount();
            doReturn(newAccount).when(accountRepository).getByEmail(EMAIL);
        }

        @Test
        public void ok_Success() {
            doReturn(createDisabledAccount()).when(accountRepository).save(captor.capture());
            registrationService.registerNewUser(USERNAME, EMAIL, PASSWORD);

            Account savedAccount = captor.getValue();
            assertThat(savedAccount)
                .extracting("username", "email", "enabled")
                .contains(USERNAME, EMAIL, false);
            assertThat(savedAccount.getRoles())
                .containsExactly(ROLE);

            verify(accountRepository).save(any());
            verify(eventsPublisher).accountRegistered(savedAccount, RegistrationMethod.EMAIL);
        }
    }

    @Nested
    @DisplayName("confirmRegistration tests")
    class ConfirmRegistrationTests {

        @AfterEach
        public void afterEach() {
            verify(tokenRepository).findById(TOKEN);
        }

        @Test
        public void ok_Success() throws TokenNotValidException {
            Account account = createDisabledAccount();
            VerificationToken token = createToken(account);
            doReturn(Optional.of(token)).when(tokenRepository).findById(TOKEN);
            doReturn(account).when(accountRepository).save(captor.capture());

            registrationService.confirmRegistration(TOKEN);

            verify(accountRepository).save(any());
            assertThat(captor.getValue().isEnabled()).isTrue();
        }

        @Test
        public void noSuchToken_TokenNotValidException() {
            doReturn(Optional.empty()).when(tokenRepository).findById(TOKEN);
            assertThrows(TokenNotValidException.class,
                () -> registrationService.confirmRegistration(TOKEN));
            verify(accountRepository, never()).save(any());
        }

        @Test
        public void accountIsEnabled_TokenNotValidException() {
            Account account = createEnabledAccount();
            VerificationToken token = createToken(account);
            doReturn(Optional.of(token)).when(tokenRepository).findById(TOKEN);
            assertThrows(TokenNotValidException.class,
                () -> registrationService.confirmRegistration(TOKEN));
            verify(accountRepository, never()).save(any());
        }

        @Test
        public void tokenIsExpired_TokenExpiredException() {
            Account account = createDisabledAccount();
            VerificationToken token = createExpiredToken(account);
            doReturn(Optional.of(token)).when(tokenRepository).findById(TOKEN);
            assertThrows(TokenExpiredException.class,
                () -> registrationService.confirmRegistration(TOKEN));
            verify(accountRepository, never()).save(any());
        }
    }

    private Account createDisabledAccount() {
        Account account = new Account(USERNAME, EMAIL, PASSWORD, ROLE);
        ReflectionTestUtils.setField(account, "id", ACCOUNT_ID);
        return account;
    }

    private Account createEnabledAccount() {
        Account account = createDisabledAccount();
        account.setEnabled(true);
        return account;
    }

    private VerificationToken createToken(Account account) {
        VerificationToken token = new VerificationToken(account);
        ReflectionTestUtils.setField(token, "token", TOKEN);
        token.prePersist();
        return token;
    }

    private VerificationToken createExpiredToken(Account account) {
        VerificationToken token = createToken(account);
        int tokenTTL = (int) ReflectionTestUtils.getField(VerificationToken.class, "EXPIRATION_DAYS");
        Instant creationDate = (Instant) ReflectionTestUtils.getField(token, "creationDate");
        Instant expirationDate = (Instant) ReflectionTestUtils.getField(token, "expirationDate");
        ReflectionTestUtils.setField(token, "creationDate", creationDate.minus(tokenTTL + 1, ChronoUnit.DAYS));
        ReflectionTestUtils.setField(token, "expirationDate", expirationDate.minus(tokenTTL + 1, ChronoUnit.DAYS));
        return token;
    }
}
