TRUNCATE account CASCADE;
TRUNCATE role CASCADE;

ALTER SEQUENCE hibernate_sequence RESTART WITH 1;

DO $$
    DECLARE
        _user_role_id  BIGINT;
        _admin_role_id BIGINT;
        _user_id       BIGINT;
        _admin_id      BIGINT;
    BEGIN
        _user_role_id = nextval('hibernate_sequence');
        _admin_role_id = nextval('hibernate_sequence');
        _user_id = nextval('hibernate_sequence');
        _admin_id = nextval('hibernate_sequence');
        INSERT INTO role
        VALUES (_user_role_id, 'ROLE_USER'),
               (_admin_role_id, 'ROLE_ADMIN');
        INSERT INTO account
        VALUES (_user_id, true,
                '$2a$10$btyWAeuvmWIPZEei/9HEmumIlsvgZzdkd9639mlV8PUBSgdTzrad.',
                'TestUser',
                'anshelen2@gmail.com'),
               (_admin_id, true,
                '$2a$10$TXhMo/gzgLz.fAyM7C3g0.mvUKauw8LwUGHRyRES4.DtIdRSlNmsG',
                'Admin',
                'anshelen@yandex.ru');
        INSERT INTO account_roles
        VALUES (_user_id, _user_role_id),
               (_admin_id, _admin_role_id);
    END $$;


