package dev.shelenkov.portfolio.service.registration;

import dev.shelenkov.portfolio.model.Account;
import dev.shelenkov.portfolio.model.Role;
import dev.shelenkov.portfolio.model.VerificationToken;
import dev.shelenkov.portfolio.repository.AccountRepository;
import dev.shelenkov.portfolio.repository.RoleRepository;
import dev.shelenkov.portfolio.repository.VerificationTokenRepository;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RegistrationService.class, BCryptPasswordEncoder.class})
@DisplayName("RegistrationService tests")
class RegistrationServiceTests {

    private static final Long ACCOUNT_ID = 1L;
    private static final String EMAIL = "anshelen@yandex.ru";
    private static final String USERNAME = "name";
    private static final String PASSWORD = "password";
    private static final String ROLE_NAME = "ROLE_USER";
    private static final UUID TOKEN = UUID.randomUUID();

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private VerificationTokenRepository tokenRepository;

    @MockBean
    private RegistrationListener registrationListener;

    @Captor
    private ArgumentCaptor<Account> captor;

    @AfterEach
    public void afterAll() {
        reset(accountRepository, roleRepository, tokenRepository, registrationListener);
    }

    @Nested
    @DisplayName("registerNewUser tests")
    class RegisterNewUserTests {

        @BeforeEach
        public void beforeEach() {
            Account newAccount = createDisabledAccount();
            doReturn(createRole()).when(roleRepository).getByName(ROLE_NAME);
            doReturn(newAccount).when(accountRepository).getByEmail(EMAIL);
        }

        @AfterEach
        public void afterEach() {
            verify(accountRepository).existsByEmail(EMAIL);
        }

        @Test
        public void ok_Success() {
            doReturn(createDisabledAccount()).when(accountRepository).save(captor.capture());
            registrationService.registerNewUser(USERNAME, EMAIL, PASSWORD);

            Account savedAccount = captor.getValue();
            assertThat(savedAccount)
                .extracting("username", "email", "enabled")
                .contains(USERNAME, EMAIL, false);
            assertThat(passwordEncoder.matches(PASSWORD, savedAccount.getPassword()))
                .isTrue();
            assertThat(savedAccount.getRoles())
                .hasSize(1)
                .extracting("name")
                .containsOnly(ROLE_NAME);

            verify(accountRepository).save(any());
            verify(registrationListener).onApplicationEvent(any());
        }

        @Test
        public void existsAccountWithSuchEmail_IllegalArgumentException() {
            doReturn(true).when(accountRepository).existsByEmail(EMAIL);
            assertThrows(IllegalArgumentException.class,
                () -> registrationService.registerNewUser(USERNAME, EMAIL, PASSWORD));
            verify(accountRepository, never()).save(any());
            verify(registrationListener, never()).onApplicationEvent(any());
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

    @Nested
    @DisplayName("sendConfirmationEmail tests")
    class SendConfirmationEmailTests {

        @Captor
        private ArgumentCaptor<OnRegistrationCompleteEvent> eventCaptor;

        @Test
        public void ok_Success() {
            doReturn(createDisabledAccount()).when(accountRepository).getByEmail(EMAIL);
            doNothing().when(registrationListener).onApplicationEvent(eventCaptor.capture());

            registrationService.sendConfirmationEmail(EMAIL);

            OnRegistrationCompleteEvent event = eventCaptor.getValue();
            assertThat(event.getAccountId()).isEqualTo(ACCOUNT_ID);

            verify(accountRepository, times(2)).getByEmail(EMAIL);
            verify(registrationListener).onApplicationEvent(any());
        }

        @Test
        public void noAccountWithProvidedEmail_IllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                () -> registrationService.sendConfirmationEmail(EMAIL));
            verify(accountRepository).getByEmail(EMAIL);
            verify(registrationListener, never()).onApplicationEvent(any());
        }

        @Test
        public void accountIsEnabled_IllegalArgumentException() {
            doReturn(createEnabledAccount()).when(accountRepository).getByEmail(EMAIL);
            assertThrows(IllegalArgumentException.class,
                () -> registrationService.sendConfirmationEmail(EMAIL));
            verify(accountRepository).getByEmail(EMAIL);
            verify(registrationListener, never()).onApplicationEvent(any());
        }

    }

    @Nested
    @DisplayName("canSendConfirmationEmail tests")
    class CanSendConfirmationEmailTests {

        @AfterEach
        public void afterEach() {
            verify(accountRepository).getByEmail(EMAIL);
        }

        @Test
        public void ok_True() {
            doReturn(createDisabledAccount()).when(accountRepository).getByEmail(EMAIL);
            boolean result = registrationService.canSendConfirmationEmail(EMAIL);
            assertThat(result).isTrue();
        }

        @Test
        public void noAccountWithProvidedEmail_False() {
            boolean result = registrationService.canSendConfirmationEmail(EMAIL);
            assertThat(result).isFalse();
        }

        @Test
        public void accountIsEnabled_False() {
            doReturn(createEnabledAccount()).when(accountRepository).getByEmail(EMAIL);
            boolean result = registrationService.canSendConfirmationEmail(EMAIL);
            assertThat(result).isFalse();
        }

    }

    private Role createRole() {
        return new Role(ROLE_NAME);
    }

    private Account createDisabledAccount() {
        Account account = new Account(USERNAME, EMAIL, PASSWORD, createRole());
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
        ReflectionTestUtils.setField(token, "creationDate", creationDate.minus(tokenTTL, ChronoUnit.DAYS));
        ReflectionTestUtils.setField(token, "expirationDate", expirationDate.minus(tokenTTL, ChronoUnit.DAYS));
        return token;
    }
}