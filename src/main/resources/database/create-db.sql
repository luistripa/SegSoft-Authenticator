
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS resources;
DROP TABLE IF EXISTS operations;
DROP TABLE IF EXISTS role_permissions;
DROP TABLE IF EXISTS account_permissions;


CREATE TABLE roles (
    role_id TEXT PRIMARY KEY
);

CREATE TABLE accounts (
    username TEXT PRIMARY KEY ,
    password_hash TEXT NOT NULL,
    is_locked BOOLEAN NOT NULL,
    logged_in BOOLEAN NOT NULL DEFAULT FALSE,
    role_id TEXT NOT NULL DEFAULT 'Authors',

    FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

CREATE TABLE operations (
    operation_id TEXT PRIMARY KEY
);

CREATE TABLE role_permissions (
    role_id TEXT NOT NULL,
    resource_id INTEGER NOT NULL,
    operation_id TEXT NOT NULL,

    PRIMARY KEY (role_id, resource_id, operation_id),
    FOREIGN KEY (role_id) REFERENCES roles(role_id),
    FOREIGN KEY (resource_id) REFERENCES page(page_id),
    FOREIGN KEY (operation_id) REFERENCES operations(operation_id)
);

CREATE TABLE account_permissions (
    username TEXT NOT NULL,
    resource_id INTEGER NOT NULL,
    operation_id TEXT NOT NULL,

    FOREIGN KEY (username) REFERENCES accounts(username),
    FOREIGN KEY (resource_id) REFERENCES page(page_id),
    FOREIGN KEY (operation_id) REFERENCES operations(operation_id)
);


-- Create root account with default password 'password'
INSERT INTO accounts (username, password_hash, logged_in, is_locked, role_id) VALUES ('root', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', false, false, 'Admins');

-- Create roles
INSERT INTO roles VALUES ('Authors');
INSERT INTO roles VALUES ('Admins');

-- Create operations
INSERT INTO operations VALUES ('READ_PAGE');
INSERT INTO operations VALUES ('SUBMIT_FOLLOW_REQUEST');
INSERT INTO operations VALUES ('APPROVE_FOLLOW_REQUEST');
INSERT INTO operations VALUES ('READ_POST');
INSERT INTO operations VALUES ('CREATE_POST');
INSERT INTO operations VALUES ('DELETE_POST');
INSERT INTO operations VALUES ('LIKE_UNLIKE_POST');
INSERT INTO operations VALUES ('CREATE_PAGE');
INSERT INTO operations VALUES ('DELETE_PAGE');
