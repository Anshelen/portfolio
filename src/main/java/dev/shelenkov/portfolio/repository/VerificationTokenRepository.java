package dev.shelenkov.portfolio.repository;

import dev.shelenkov.portfolio.domain.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VerificationTokenRepository
    extends JpaRepository<VerificationToken, UUID> {
}
