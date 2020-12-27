package dev.shelenkov.portfolio.repository;

import dev.shelenkov.portfolio.domain.Country;
import dev.shelenkov.portfolio.support.EnablePostgresContainer;
import dev.shelenkov.portfolio.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnablePostgresContainer
@IntegrationTest
public class CountryRepositoryTests {

    @Autowired
    private CountryRepository countryRepository;

    @Test
    public void getByCode_noSuchCode_emptyOptional() {
        Optional<Country> result = countryRepository.getByCode("RU");
        assertThat(result).isEmpty();
    }

    @Sql(statements = "INSERT INTO country(id, code, name) VALUES (1, 'RU', 'Russia')")
    @Test
    public void getByCode_suchCodeExists_optionalWithData() {
        Country expected = new Country("RU", "Russia");
        expected.setId(1L);

        Optional<Country> result = countryRepository.getByCode("RU");

        assertThat(result).contains(expected);
    }
}
