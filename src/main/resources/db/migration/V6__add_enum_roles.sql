CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');

ALTER TABLE account ADD COLUMN roles user_role[];

UPDATE account
SET roles = ur.user_role_arr
FROM (
         SELECT a.id AS user_id, array_agg(substr(r.name, 6)::user_role) AS user_role_arr
         FROM account a
                  LEFT JOIN account_roles ar ON a.id = ar.accounts_id
                  LEFT JOIN role r ON ar.roles_id = r.id
         GROUP BY a.id) AS ur
WHERE id = ur.user_id;

DROP TABLE account_roles;

DROP TABLE role;
