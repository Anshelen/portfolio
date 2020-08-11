ALTER TABLE account ADD CONSTRAINT account_unique_email_constraint UNIQUE (email);
ALTER TABLE account ALTER COLUMN email SET NOT NULL;

ALTER TABLE account ADD COLUMN github_id TEXT;
ALTER TABLE account ADD COLUMN google_id TEXT;

ALTER TABLE account ADD CONSTRAINT account_unique_github_id_constraint UNIQUE (github_id);
ALTER TABLE account ADD CONSTRAINT account_unique_google_id_constraint UNIQUE (google_id);
