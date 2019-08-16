package dev.shelenkov.portfolio.model;

import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Immutable
public class VerificationToken {

    private static final int EXPIRATION_DAYS = 1;

    @Id
    @GeneratedValue
    private UUID token;

    @OneToOne
    @JoinColumn(nullable = false)
    private Account account;

    private Instant creationDate;

    private Instant expirationDate;

    @SuppressWarnings("unused")
    public VerificationToken() {
    }

    public VerificationToken(Account account) {
        this.account = account;
    }

    @PrePersist
    public void prePersist() {
        creationDate = Instant.now();
        expirationDate = creationDate.plus(EXPIRATION_DAYS, ChronoUnit.DAYS);
    }

    public UUID getToken() {
        return token;
    }

    public Account getAccount() {
        return account;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public Instant getExpirationDate() {
        return expirationDate;
    }
}
