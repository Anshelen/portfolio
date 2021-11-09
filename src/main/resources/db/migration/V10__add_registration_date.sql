ALTER TABLE account
    ADD COLUMN registration_date TIMESTAMP NOT NULL DEFAULT now();
