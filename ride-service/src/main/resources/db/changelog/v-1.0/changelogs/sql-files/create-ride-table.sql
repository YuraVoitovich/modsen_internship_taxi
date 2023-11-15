CREATE TABLE IF NOT EXISTS ride (
    id UUID PRIMARY KEY,
    passenger_profile_id UUID NOT NULL,
    driver_profile_id UUID NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    driver_rating NUMERIC NOT NULL,
    passenger_rating NUMERIC NOT NULL
);
