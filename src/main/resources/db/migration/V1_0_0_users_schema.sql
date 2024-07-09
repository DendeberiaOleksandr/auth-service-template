CREATE TABLE users IF NOT EXISTS (
    id BIGINT NOT NULL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password TEXT NOT NULL,
    enabled BOOL NOT NULL DEFAULT true
);