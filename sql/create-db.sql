

CREATE TABLE IF NOT EXISTS accounts (
    username TEXT PRIMARY KEY ,
    password_hash TEXT NOT NULL,
    is_locked BOOLEAN NOT NULL,
    logged_in BOOLEAN NOT NULL DEFAULT FALSE
);