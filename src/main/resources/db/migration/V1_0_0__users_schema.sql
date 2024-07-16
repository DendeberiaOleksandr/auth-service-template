CREATE TABLE users (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password TEXT NOT NULL,
    enabled BOOL NOT NULL DEFAULT true,
    status VARCHAR(255) NOT NULL DEFAULT 'UNVERIFIED',
    activation_code VARCHAR(255),
    activation_code_last_sent_at TIMESTAMP,
    activation_code_sent_times INTEGER DEFAULT 0,
    invalid_activation_code_entered_times INTEGER DEFAULT 0,
    invalid_activation_code_entered_last_time_at TIMESTAMP,
    reset_password_code VARCHAR(255),
    reset_password_code_last_sent_at TIMESTAMP,
    reset_password_sent_times INTEGER DEFAULT 0,
    invalid_reset_password_code_entered_times INTEGER DEFAULT 0,
    invalid_reset_password_code_entered_last_time_at TIMESTAMP
);