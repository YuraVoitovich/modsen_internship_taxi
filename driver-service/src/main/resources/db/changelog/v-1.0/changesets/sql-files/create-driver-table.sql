CREATE TABLE IF NOT EXISTS driver_profile (
    id UUID PRIMARY KEY,
    sub UUID,
    phone_number VARCHAR(13) NOT NULL UNIQUE,
    name VARCHAR(255),
    surname VARCHAR(255),
    experience INT
);
