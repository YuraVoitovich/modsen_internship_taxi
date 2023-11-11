ALTER TABLE rating
ADD CONSTRAINT fk_driver_profile
FOREIGN KEY (driver_profile_id) REFERENCES driver_profile(id);