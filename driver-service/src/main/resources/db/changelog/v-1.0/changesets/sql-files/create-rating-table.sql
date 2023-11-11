CREATE TABLE IF NOT EXISTS rating (
      id UUID PRIMARY KEY,
      rate_value INT NOT NULL,
      passenger_id UUID,
      driver_profile_id UUID
);