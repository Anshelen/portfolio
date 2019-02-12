CREATE SEQUENCE hibernate_sequence;

CREATE TABLE role (
    id   BIGINT NOT NULL
        CONSTRAINT role_pkey
        PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE account (
    id       BIGINT  NOT NULL
        CONSTRAINT account_pkey
        PRIMARY KEY,
    enabled  BOOLEAN NOT NULL,
    password VARCHAR(72),
    username VARCHAR(255),
    email    VARCHAR(255)
);

CREATE TABLE account_roles (
    accounts_id BIGINT NOT NULL
        CONSTRAINT account_id_fk
        REFERENCES account,
    roles_id    BIGINT NOT NULL
        CONSTRAINT roles_id_fk
        REFERENCES role,
    CONSTRAINT account_roles_pkey
    PRIMARY KEY (accounts_id, roles_id)
);

INSERT INTO role
VALUES (1, 'ROLE_USER'),
       (2, 'ROLE_ADMIN');
-- For user password 'user', for admin - 'admin'
INSERT INTO account
VALUES (3, true, '$2a$10$U2ah6UZnMrhVQ0sYrcQOveAj6x9aX8BO5MxZaA.53gGNKBeaEQdLW', 'User', 'user@mail.ru'),
       (4, true, '$2a$10$KiT2cQFurpDtnf5.5Ggqf.t7FU37xWSFyaAVBB4NTkWFo30ld4UIW', 'Admin', 'admin@mail.ru');
INSERT INTO account_roles
VALUES (3, 1),
       (4, 2);
