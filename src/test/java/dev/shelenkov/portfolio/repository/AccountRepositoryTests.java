package dev.shelenkov.portfolio.repository;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.domain.Country;
import dev.shelenkov.portfolio.domain.Role;
import dev.shelenkov.portfolio.support.EnablePostgresContainer;
import dev.shelenkov.portfolio.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

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

    @Test
    public void getByIdWithCountries_noSuchAccount_null() {
        Account result = accountRepository.getByIdWithCountries(1L);
        assertThat(result).isNull();
    }

    @Sql(statements = {
        "INSERT INTO country(id, code, name) VALUES (1, 'RU', 'Russia'), (2, 'US', 'USA')",
        "INSERT INTO account(id, enabled, password, username, email, roles) VALUES (1, true, 'pass', 'username', 'email@mail.ru', ARRAY['USER'::user_role])",
        "INSERT INTO account_x_country(account_id, country_id) VALUES (1, 1), (1, 2)"
    })
    @Test
    public void getByIdWithCountries_suchAccountExists_accountWithCountries() {
        Country russia = new Country("RU", "Russia");
        russia.setId(1L);
        Country usa = new Country("US", "USA");
        usa.setId(2L);

        Account expected = new Account("username", "email@mail.ru", "pass", Role.USER);
        expected.setId(1L);
        expected.setEnabled(true);
        expected.addLoginCountry(russia);
        expected.addLoginCountry(usa);

        Account result = accountRepository.getByIdWithCountries(1L);

        assertThat(result).isEqualTo(expected);
    }
}
