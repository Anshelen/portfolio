CREATE SEQUENCE country_id_seq INCREMENT 1;

CREATE TABLE country
(
    id   BIGINT
        CONSTRAINT country_pkey PRIMARY KEY,
    code VARCHAR(3) UNIQUE NOT NULL,
    name VARCHAR(50)       NOT NULL
);

CREATE TABLE account_x_country
(
    account_id BIGINT
        CONSTRAINT account_x_country_account_fk REFERENCES account (id),
    country_id BIGINT
        CONSTRAINT account_x_country_country_fk REFERENCES country (id),
    CONSTRAINT account_x_country_unique UNIQUE (account_id, country_id)
);
