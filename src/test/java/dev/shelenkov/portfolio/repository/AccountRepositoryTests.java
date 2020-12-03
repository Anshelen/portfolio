package dev.shelenkov.portfolio.repository;

import dev.shelenkov.portfolio.support.EnablePostgresContainer;
import dev.shelenkov.portfolio.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnablePostgresContainer
@IntegrationTest
public class AccountRepositoryTests {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void existsByEmail_existingEmail_true() {
        boolean result = accountRepository.existsByEmail("anshelen@yandex.ru");
        assertThat(result).isTrue();
    }

    @Test
    public void existsByEmail_notExistingEmail_false() {
        boolean result = accountRepository.existsByEmail("other@yandex.ru");
        assertThat(result).isFalse();
    }
}
