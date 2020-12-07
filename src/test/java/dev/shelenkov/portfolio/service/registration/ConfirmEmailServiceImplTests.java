package dev.shelenkov.portfolio.service.registration;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.domain.Role;
import dev.shelenkov.portfolio.domain.VerificationToken;
import dev.shelenkov.portfolio.repository.AccountRepository;
import dev.shelenkov.portfolio.repository.VerificationTokenRepository;
import dev.shelenkov.portfolio.service.email.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(ConfirmEmailServiceImpl.class)
@DisplayName("ConfirmEmailService tests")
public class ConfirmEmailServiceImplTests {

    @Autowired
    private ConfirmEmailService confirmEmailService;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private VerificationTokenRepository tokenRepository;

    @MockBean
    private EmailService emailService;

    @Captor
    private ArgumentCaptor<VerificationToken> tokenCaptor;

    @BeforeEach
    public void setUp() {
        when(tokenRepository.save(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
    }

    @Test
    public void sendConfirmationEmailLong_noSuchAccount_IllegalStateException() throws IOException {
        expectNoAccountWithId(1L);

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(
            () -> confirmEmailService.sendConfirmationEmail(1L));

        assertNoEmailWasSent();
    }

    @Test
    public void sendConfirmationEmailLong_enabledAccount_IllegalStateException() throws IOException {
        expectAccountExistsInDB(createEnabledAccount(1L));

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(
            () -> confirmEmailService.sendConfirmationEmail(1L));

        assertNoEmailWasSent();
    }

    @Test
    public void sendConfirmationEmailLong_disabledAccount_sendEmail() throws IOException {
        Account account = createDisabledAccount(1L);

        expectAccountExistsInDB(account);
        confirmEmailService.sendConfirmationEmail(1L);
        assertEmailWasSentForAccount(account);
    }

    @Test
    public void sendConfirmationEmailString_noSuchAccount_IllegalStateException() throws IOException {
        expectNoAccountWithEmail("email@mail.ru");

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(
            () -> confirmEmailService.sendConfirmationEmail("email@mail.ru"));

        assertNoEmailWasSent();
    }

    @Test
    public void sendConfirmationEmailString_enabledAccount_IllegalStateException() throws IOException {
        expectAccountExistsInDB(createEnabledAccount("email@mail.ru"));

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(
            () -> confirmEmailService.sendConfirmationEmail("email@mail.ru"));

        assertNoEmailWasSent();
    }

    @Test
    public void sendConfirmationEmailString_disabledAccount_sendEmail() throws IOException {
        Account account = createDisabledAccount("email@mail.ru");

        expectAccountExistsInDB(account);
        confirmEmailService.sendConfirmationEmail("email@mail.ru");
        assertEmailWasSentForAccount(account);
    }

    @Test
    public void isSendConfirmationEmailForbidden_noSuchAccount_true() {
        expectNoAccountWithEmail("email@mail.ru");
        boolean result = confirmEmailService.isSendConfirmationEmailForbidden("email@mail.ru");
        assertThat(result).isTrue();
    }

    @Test
    public void isSendConfirmationEmailForbidden_enabledAccount_true() {
        expectAccountExistsInDB(createEnabledAccount("email@mail.ru"));
        boolean result = confirmEmailService.isSendConfirmationEmailForbidden("email@mail.ru");
        assertThat(result).isTrue();
    }

    @Test
    public void isSendConfirmationEmailForbidden_disabledAccount_false() {
        expectAccountExistsInDB(createDisabledAccount("email@mail.ru"));
        boolean result = confirmEmailService.isSendConfirmationEmailForbidden("email@mail.ru");
        assertThat(result).isFalse();
    }

    private Account createEnabledAccount(long id) {
        Account account = createDisabledAccount(id);
        account.setEnabled(true);
        return account;
    }

    private Account createEnabledAccount(String email) {
        Account account = createDisabledAccount(email);
        account.setEnabled(true);
        return account;
    }

    private Account createDisabledAccount(long id) {
        Account account = new Account("USERNAME", "email@mail.ru", "PASSWORD", Role.USER);
        ReflectionTestUtils.setField(account, "id", id);
        return account;
    }

    private Account createDisabledAccount(String email) {
        Account account = new Account("USERNAME", email, "PASSWORD", Role.USER);
        ReflectionTestUtils.setField(account, "id", 1L);
        return account;
    }

    private void expectNoAccountWithId(long id) {
        doReturn(Optional.empty()).when(accountRepository).findById(id);
    }

    private void expectNoAccountWithEmail(String email) {
        doReturn(null).when(accountRepository).getByEmail(email);
    }

    private void expectAccountExistsInDB(Account account) {
        doReturn(Optional.of(account)).when(accountRepository).findById(account.getId());
        doReturn(account).when(accountRepository).getByEmail(account.getEmail());
    }

    private void assertNoEmailWasSent() throws IOException {
        verify(emailService, never()).sendConfirmationEmail(any());
    }

    private void assertEmailWasSentForAccount(Account account) throws IOException {
        verify(emailService).sendConfirmationEmail(tokenCaptor.capture());
        VerificationToken token = tokenCaptor.getValue();
        verify(tokenRepository).save(token);
        assertThat(token.getAccount()).isEqualTo(account);
    }
}
