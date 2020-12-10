CREATE SEQUENCE account_id_seq INCREMENT 10;
SELECT setval('account_id_seq', (select max(id) from account));
DROP SEQUENCE hibernate_sequence;
