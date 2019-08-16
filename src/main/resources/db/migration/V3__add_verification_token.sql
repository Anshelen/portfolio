create table verification_token (
    token           UUID
        CONSTRAINT verification_token_pkey
        PRIMARY KEY,
    creation_date   TIMESTAMP,
    expiration_date TIMESTAMP,
    account_id      BIGINT NOT NULL
        CONSTRAINT verification_token_account_fk
        REFERENCES account
);
