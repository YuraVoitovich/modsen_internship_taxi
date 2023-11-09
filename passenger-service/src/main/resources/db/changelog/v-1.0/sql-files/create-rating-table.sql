CREATE TABLE IF NOT EXISTS rating (
      id UUID PRIMARY KEY,
      rate_value INT NOT NULL,
      driver_id UUID,
      passenger_profile_id UUID
);