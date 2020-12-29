package dev.shelenkov.portfolio.repository;

import dev.shelenkov.portfolio.domain.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {

    Optional<Country> getByCode(String code);
}
