CREATE TABLE IF NOT EXISTS rating (
      id UUID PRIMARY KEY,
      rate_value DECIMAL NOT NULL,
      driver_id UUID NOT NULL,
      passenger_profile_id UUID NOT NULL
);