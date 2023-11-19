CREATE TABLE IF NOT EXISTS ride (
    id UUID PRIMARY KEY,
    passenger_profile_id UUID NOT NULL,
    driver_profile_id UUID,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    driver_rating NUMERIC,
    passenger_rating NUMERIC,
    start_geo geometry not null,
    end_geo geometry not null,
    status varchar(20) not null,
    passenger_position geometry,
    driver_position geometry
);
