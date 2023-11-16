ALTER TABLE rating
ADD CONSTRAINT fk_passenger_profile
FOREIGN KEY (passenger_profile_id) REFERENCES passenger_profile(id);