-- Пример добавления тестовых данных в таблицу "ride"

-- Запись для confirmRideStart_driverLocationIsNotValid_shouldReturnRideStartConfirmationErrorResponse()
INSERT INTO ride (id, passenger_profile_id, driver_profile_id, start_date, end_date, driver_rating, passenger_rating, start_geo, end_geo, status, passenger_position, driver_position)
VALUES
    ('4ba65be8-cd97-4d40-aeae-8eb5a71fa50c', '4ba65be8-cd97-4d40-aeae-8eb5a71fa58c', '025fe6d1-8363-4a1a-925d-d91a8b640b8f', null, null, 4.5, 5.0, ST_GeomFromText('POINT(40.7128 -74.0060)'), ST_GeomFromText('POINT(40.7128 -74.0460)'), 'ACCEPTED', ST_GeomFromText('POINT(15 25)'), ST_GeomFromText('POINT(40.7128 -74.0064)'));

-- Запись для confirmRideStart_notValidRideStatus_shouldReturnRideStartConfirmationErrorResponse()
INSERT INTO ride (id, passenger_profile_id, driver_profile_id, start_date, end_date, driver_rating, passenger_rating, start_geo, end_geo, status, passenger_position, driver_position)
VALUES
    ('4ba65be8-cd97-4d40-aeae-8eb5a71fa51c', 'f00a8f6f-9294-4e4e-aa4d-42f801b69a95', '6d8a8f9a-7f9d-4c71-8b4c-2e0e487b3262', null, null, 4.2, 4.8, ST_GeomFromText('POINT(40.7128 -74.0060)'), ST_GeomFromText('POINT(40.7128 -74.0460)'), 'IN_PROGRESS', ST_GeomFromText('POINT(15 25)'), ST_GeomFromText('POINT(40.7128 -74.0064)'));

-- Запись для confirmRideStart_validRideCondition_shouldConfirmRideStart()
INSERT INTO ride (id, passenger_profile_id, driver_profile_id, start_date, end_date, driver_rating, passenger_rating, start_geo, end_geo, status, passenger_position, driver_position)
VALUES
    ('4ba65be8-cd97-4d40-aeae-8eb5a71fa52c', 'd1e42c0d-aa71-448c-843f-5a5e801ed221', '7e4f5342-cb2b-4e8c-8ab7-1629afcf5d0d', '2023-01-03 09:45:00', '2023-01-03 11:30:00', 3.8, 4.0, ST_GeomFromText('POINT(40.7128 -74.0060)'), ST_GeomFromText('POINT(40.7128 -74.0460)'), 'ACCEPTED', ST_GeomFromText('POINT(28 38)'), ST_GeomFromText('POINT(40.7128 -74.0061)'));

-- Запись для проверки дефолтного радиуса в 600 и для acceptRide_correctRequest_shouldAcceptRide()
INSERT INTO ride (id, passenger_profile_id, driver_profile_id, start_date, end_date, driver_rating, passenger_rating, start_geo, end_geo, status, passenger_position, driver_position)
VALUES
    ('4ba65be8-cd97-4d40-aeae-8eb5a71fa53c', 'fe619349-6734-4b2e-b949-0657af63b9d8', '1f55fcf8-8a9d-45fb-bbbf-5f5ebea7b8f4', '2023-01-04 16:00:00', '2023-01-04 18:00:00', 4.8, 4.5, ST_GeomFromText('POINT(40.1128 -74.009)'), ST_GeomFromText('POINT(60.0 70.0)'), 'REQUESTED', ST_GeomFromText('POINT(45 55)'), ST_GeomFromText('POINT(65 75)'));

-- Запись для проверки дефолтного радиуса в 600 и проверки введенного радиуса 500
INSERT INTO ride (id, passenger_profile_id, driver_profile_id, start_date, end_date, driver_rating, passenger_rating, start_geo, end_geo, status, passenger_position, driver_position)
VALUES
    ('4ba65be8-cd97-4d40-aeae-8eb5a71fa54c', 'f7d49a65-1b28-4eab-8fc4-4a4d8f5e80cc', '2c2a12c8-6a9a-4f48-82aa-0d4fc35c81e2', '2023-01-05 10:30:00', '2023-01-05 12:30:00', 4.2, 4.0, ST_GeomFromText('POINT(40.1128 -74.008)'), ST_GeomFromText('POINT(50.0 60.0)'), 'REQUESTED', ST_GeomFromText('POINT(35 45)'), ST_GeomFromText('POINT(55 65)'));

-- Запись 6
INSERT INTO ride (id, passenger_profile_id, driver_profile_id, start_date, end_date, driver_rating, passenger_rating, start_geo, end_geo, status, passenger_position, driver_position)
VALUES
    ('4ba65be8-cd97-4d40-aeae-8eb5a71fa55c', 'd92ef591-e94c-42a9-8ac1-4e75d137ff0e', '3e722f53-d19f-4c34-9d86-3b032157d167', '2023-01-06 08:15:00', '2023-01-06 09:45:00', 3.5, 4.2, ST_GeomFromText('POINT(20 30)'), ST_GeomFromText('POINT(40 50)'), 'CANCELED', ST_GeomFromText('POINT(25 35)'), ST_GeomFromText('POINT(45 55)'));



-- Запись для confirmRideEnd_driverLocationIsNotValid_shouldReturnRideEndConfirmationErrorResponse()
INSERT INTO ride (id, passenger_profile_id, driver_profile_id, start_date, end_date, driver_rating, passenger_rating, start_geo, end_geo, status, passenger_position, driver_position)
VALUES
    ('4ba65be8-cd97-4d40-aeae-8eb5a71fa56c', '4ba65be8-cd97-4d40-aeae-8eb5a71fa58c', '025fe6d1-8363-4a1a-925d-d91a8b640b8f', '2023-01-01 12:00:00', '2023-01-01 14:00:00', 4.5, 5.0, ST_GeomFromText('POINT(40.7128 -74.0060)'), ST_GeomFromText('POINT(40.7128 -74.0460)'), 'IN_PROGRESS', ST_GeomFromText('POINT(40.7128 -74.0060)'), ST_GeomFromText('POINT(40.7128 -74.0064)'));

-- Запись для confirmRideEnd_notValidRideStatus_shouldReturnRideEndConfirmationErrorResponse()
INSERT INTO ride (id, passenger_profile_id, driver_profile_id, start_date, end_date, driver_rating, passenger_rating, start_geo, end_geo, status, passenger_position, driver_position)
VALUES
    ('4ba65be8-cd97-4d40-aeae-8eb5a71fa57c', 'f00a8f6f-9294-4e4e-aa4d-42f801b69a95', '6d8a8f9a-7f9d-4c71-8b4c-2e0e487b3262', '2023-01-02 14:30:00', '2023-01-02 16:30:00', 4.2, 4.8, ST_GeomFromText('POINT(40.7128 -74.0060)'), ST_GeomFromText('POINT(40.7128 -74.0460)'), 'ACCEPTED', ST_GeomFromText('POINT(40.7128 -74.0060)'), ST_GeomFromText('POINT(40.7128 -74.0064)'));

-- Запись для confirmRideEnd_validRideCondition_shouldConfirmRideEnd()
INSERT INTO ride (id, passenger_profile_id, driver_profile_id, start_date, end_date, driver_rating, passenger_rating, start_geo, end_geo, status, passenger_position, driver_position)
VALUES
    ('4ba65be8-cd97-4d40-aeae-8eb5a71fa58c', 'd1e42c0d-aa71-448c-843f-5a5e801ed291', '7e4f5342-cb2b-4e8c-8ab7-1629afcf5d0d', '2023-01-03 09:45:00', '2023-01-03 11:30:00', 3.8, 4.0, ST_GeomFromText('POINT(40.7128 -74.0060)'), ST_GeomFromText('POINT(40.7128 -74.0460)'), 'IN_PROGRESS', ST_GeomFromText('POINT(40.7128 -74.0060)'), ST_GeomFromText('POINT(40.7128 -74.0461)'));


-- Запись для getAllRides DriverManagement
INSERT INTO ride (id, passenger_profile_id, driver_profile_id, start_date, end_date, driver_rating, passenger_rating, start_geo, end_geo, status, passenger_position, driver_position)
VALUES
    ('4ba65be8-cd97-4d40-aeae-8eb5a71fa59c', '7e4f5342-cb2b-4e8c-8ab7-1629afcf5d01', 'd1e42c0d-aa71-448c-843f-5a5e801ed287', null, null, null, null, ST_GeomFromText('POINT(40 -74)'), ST_GeomFromText('POINT(40 -74)'), 'COMPLETED', ST_GeomFromText('POINT(40 -74)'), ST_GeomFromText('POINT(40 -74)'));

-- Запись для getAllRides DriverManagement
INSERT INTO ride (id, passenger_profile_id, driver_profile_id, start_date, end_date, driver_rating, passenger_rating, start_geo, end_geo, status, passenger_position, driver_position)
VALUES
    ('4ba65be8-cd97-4d40-aeae-8eb5a71fa60c', '7e4f5342-cb2b-4e8c-8ab7-1629afcf5d02', 'd1e42c0d-aa71-448c-843f-5a5e801ed287', null, null, null, null, ST_GeomFromText('POINT(40 -74)'), ST_GeomFromText('POINT(40 -74)'), 'COMPLETED', ST_GeomFromText('POINT(40 -74)'), ST_GeomFromText('POINT(40 -74)'));

-- Запись для getAllRides DriverManagement
INSERT INTO ride (id, passenger_profile_id, driver_profile_id, start_date, end_date, driver_rating, passenger_rating, start_geo, end_geo, status, passenger_position, driver_position)
VALUES
    ('4ba65be8-cd97-4d40-aeae-8eb5a71fa61c', '7e4f5342-cb2b-4e8c-8ab7-1629afcf5d03', 'd1e42c0d-aa71-448c-843f-5a5e801ed287', null, null, null, null, ST_GeomFromText('POINT(40 -74)'), ST_GeomFromText('POINT(40 -74)'), 'COMPLETED', ST_GeomFromText('POINT(40 -74)'), ST_GeomFromText('POINT(40 -74)'));


-- Запись для getAllRides PassengerManagement и createRide_rideForPassengerExists_shouldReturnRideCantBeStartedErrorResponse()
-- и cancelRide_correctRequest_shouldCancelRide() и rateDriver_rideStatusIsNotValid_shouldReturnSendRatingErrorResponse()
INSERT INTO ride (id, passenger_profile_id, driver_profile_id, start_date, end_date, driver_rating, passenger_rating, start_geo, end_geo, status, passenger_position, driver_position)
VALUES
    ('4ba65be8-cd97-4d40-aeae-8eb5a71fa62c', '7e4f5342-cb2b-4e8c-8ab7-1629afcf5d10', 'd1e4120d-aa71-448c-843f-5a5e801ed287', null, null, null, null, ST_GeomFromText('POINT(40 -74)'), ST_GeomFromText('POINT(40 -74)'), 'REQUESTED', ST_GeomFromText('POINT(40 -74)'), ST_GeomFromText('POINT(40 -74)'));

-- Запись для getAllRides PassengerManagement и cancelRide_rideCantBeCanceled_shouldReturnRideCantBeCanceledErrorResponse()
INSERT INTO ride (id, passenger_profile_id, driver_profile_id, start_date, end_date, driver_rating, passenger_rating, start_geo, end_geo, status, passenger_position, driver_position)
VALUES
    ('4ba65be8-cd97-4d40-aeae-8eb5a71fa63c', '7e4f5342-cb2b-4e8c-8ab7-1629afcf5d10', 'd1843c0d-aa71-448c-843f-5a5e801ed287', null, null, null, null, ST_GeomFromText('POINT(40 -74)'), ST_GeomFromText('POINT(40 -74)'), 'COMPLETED', ST_GeomFromText('POINT(40 -74)'), ST_GeomFromText('POINT(40 -74)'));

-- Запись для getAllRides PassengerManagement
INSERT INTO ride (id, passenger_profile_id, driver_profile_id, start_date, end_date, driver_rating, passenger_rating, start_geo, end_geo, status, passenger_position, driver_position)
VALUES
    ('4ba65be8-cd97-4d40-aeae-8eb5a71fa64c', '7e4f5342-cb2b-4e8c-8ab7-1629afcf5d10', 'd1242c0d-aa71-448c-843f-5a5e801ed287', null, null, null, null, ST_GeomFromText('POINT(40 -74)'), ST_GeomFromText('POINT(40 -74)'), 'COMPLETED', ST_GeomFromText('POINT(40 -74)'), ST_GeomFromText('POINT(40 -74)'));


-- Запись для rateDriver_correctRequest_shouldRateDriver() и ratePassenger_correctRequest_shouldRatePassenger()
INSERT INTO ride (id, passenger_profile_id, driver_profile_id, start_date, end_date, driver_rating, passenger_rating, start_geo, end_geo, status, passenger_position, driver_position)
VALUES
    ('4ba65be8-cd97-4d40-aeae-8eb5a71fa65c', '7e4f5342-cb2b-4e8c-8ab7-1629afcf5d11', 'd1242c0d-aa71-448c-843f-5a5e801ed289', '2023-01-01 12:00:00', null, null, null, ST_GeomFromText('POINT(40 -74)'), ST_GeomFromText('POINT(40 -74)'), 'IN_PROGRESS', ST_GeomFromText('POINT(40 -74)'), ST_GeomFromText('POINT(40 -74)'));
