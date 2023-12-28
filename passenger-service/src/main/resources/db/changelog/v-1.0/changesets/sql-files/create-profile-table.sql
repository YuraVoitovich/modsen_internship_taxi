CREATE TABLE IF NOT EXISTS passenger_profile (
    id UUID PRIMARY KEY,
    phone_number VARCHAR(13) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL
);